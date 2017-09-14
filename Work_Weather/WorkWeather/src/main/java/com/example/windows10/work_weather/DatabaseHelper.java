package com.example.windows10.work_weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Hospital_Data";

    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private String DATABASE_NURSES = ("CREATE TABLE IF NOT EXISTS nurses(id String,input INT,median DOUBLE,date String,shift_id INT,inputDate String,changed INT);");
    private String DATABASE_AVG = ("CREATE TABLE IF NOT EXISTS avgShift(shift_id INT,average DOUBLE,inputDate String);");
    private String DATABASE_ROOM_AVG = ("CREATE TABLE IF NOT EXISTS avgRoom(key_id INT,median DOUBLE,inputDate String);");
    private String DATABASE_KEY = ("CREATE TABLE IF NOT EXISTS key(key_id INT);");
    private String DATABASE_Counter = ("CREATE TABLE IF NOT EXISTS counter(key_id INT);");
    private String DATABASE_DayTracker = ("CREATE TABLE IF NOT EXISTS day(key_id INT,inputDate String);");

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_NURSES);
        database.execSQL(DATABASE_AVG);
        database.execSQL(DATABASE_ROOM_AVG);
        database.execSQL(DATABASE_KEY);
        database.execSQL(DATABASE_Counter);
        database.execSQL(DATABASE_DayTracker);

        database.execSQL("INSERT INTO avgRoom VALUES('0','0','');");
        database.execSQL("INSERT INTO avgRoom VALUES('1','0','');");
        database.execSQL("INSERT INTO avgRoom VALUES('2','0','');");

        database.execSQL("INSERT INTO day VALUES('0','');");
        database.execSQL("INSERT INTO key VALUES('0');");
        database.execSQL("INSERT INTO counter VALUES('0');");
    }

    /* Method is called during an upgrade of the database, */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from " + oldVersion +" to "+newVersion);
        database.execSQL("DROP TABLE IF EXISTS users");
        onCreate(database);
    }
}