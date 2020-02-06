package hu.holographic.todoapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

class ToDoItemDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 2;

    public ToDoItemDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE todoitem (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, year INTEGER, month INTEGER, day INTEGER, status TEXT, timestamp INTEGER)");

        long timestamp = System.currentTimeMillis();
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Android vizsgafeladat', 2020, 2, 15, 'VERY_IMPORTANT', "+timestamp+")");
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Felhívni a szerelőt', 2020, 2, 13, 'IMPORTANT', "+timestamp+")");
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Középcsapágy vásárlás', 2020, 2, 11, 'DONE', "+timestamp+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE todoitem");
        onCreate(db);
    }
}
