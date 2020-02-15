package hu.holographic.todoapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

class ToDoItemDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 4;

    public ToDoItemDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE todoitem (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, year INTEGER, month INTEGER, day INTEGER, status INTEGER, timestamp INTEGER)");

        long timestamp = System.currentTimeMillis();
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Android vizsgafeladat', 2020, 2, 15, '3', "+timestamp+")");
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Szerelő', 2020, 2, 13, '2', "+timestamp+")");
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Sört venni', 2020, 2, 17, '2', "+timestamp+")");
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Fontos!', 2020, 3, 6, '3', "+timestamp+")");
        db.execSQL("INSERT INTO todoitem (name, year, month, day, status, timestamp) VALUES ('Nem annyira fontos', 2020, 5, 4, '1', "+timestamp+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE todoitem");
        onCreate(db);
    }
}
