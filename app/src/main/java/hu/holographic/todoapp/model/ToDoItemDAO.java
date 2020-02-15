package hu.holographic.todoapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ToDoItemDAO implements IToDoItemDAO {

    private ToDoItemDBHelper helper;
    private static final String TABLE = "todoitem";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";

    public ToDoItemDAO(Context context) {
        helper = new ToDoItemDBHelper(context);
    }

    @Override
    public List<ToDoItem> getAllItems() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<ToDoItem> toDoList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            int year = cursor.getInt(cursor.getColumnIndex(YEAR));
            int month = cursor.getInt(cursor.getColumnIndex(MONTH));
            int day = cursor.getInt(cursor.getColumnIndex(DAY));
            Status status = Status.getStatusByValue(cursor.getInt(cursor.getColumnIndex(STATUS)));
            long timestamp = cursor.getLong(cursor.getColumnIndex(TIMESTAMP));
            ToDoItem tdi = new ToDoItem(id, name, year, month, day, status, timestamp);
            toDoList.add(tdi);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return toDoList;
    }

    @Override
    public List<ToDoItem> getTodaysItems() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<ToDoItem> todaysItems = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE + " WHERE " + YEAR + " = ? AND " + MONTH + " = ? AND " + DAY + " = ?"
                , new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(day)});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            Status status = Status.getStatusByValue(cursor.getInt(cursor.getColumnIndex(STATUS)));
            long timestamp = cursor.getLong(cursor.getColumnIndex(TIMESTAMP));
            ToDoItem tdi = new ToDoItem(id, name, year, month, day, status, timestamp);
            todaysItems.add(tdi);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return todaysItems;
    }

    @Override
    public void deleteItem(ToDoItem tdi) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE, ID + "=?", new String[]{tdi.getId() + ""});
        db.close();
    }

    @Override
    public void saveOrUpdateItem(ToDoItem tdi) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, tdi.getName());
        cv.put(YEAR, tdi.getYear());
        cv.put(MONTH, tdi.getMonth());
        cv.put(DAY, tdi.getDay());
        cv.put(STATUS, tdi.getStatus().value);
        cv.put(TIMESTAMP, tdi.getTimestamp());

        if (tdi.getId() == -1) { //new item, save
            long id = db.insert(TABLE, null, cv);
            tdi.setId((int) id);
        } else { //existing item, update
            db.update(TABLE, cv, ID + "=?", new String[]{tdi.getId() + ""});
        }
        db.close();
    }

    @Override
    public void deleteAllItems() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE);
        db.close();
    }
}
