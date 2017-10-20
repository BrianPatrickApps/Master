package com.example.windows10.work_weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Hospital_Data_DDW";

    private static final int DATABASE_VERSION = 2;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {

<<<<<<< HEAD
        String DATABASE_NURSES = ("CREATE TABLE IF NOT EXISTS nurses(input INT,median DOUBLE,date String,shift_id INT,inputDate String);");
=======
        String DATABASE_NURSES = ("CREATE TABLE IF NOT EXISTS nurses(id String,input INT,median DOUBLE,date String,shift_id INT,inputDate String,changed INT);");
>>>>>>> parent of 28876fa... Demo A
        database.execSQL(DATABASE_NURSES);

        String DATABASE_KEY = ("CREATE TABLE IF NOT EXISTS key(key_id INT);");
        database.execSQL(DATABASE_KEY);

        String DATABASE_COUNTER = ("CREATE TABLE IF NOT EXISTS counter(key_id INT);");
        database.execSQL(DATABASE_COUNTER);

        String DATABASE_DAY_TRACKER = ("CREATE TABLE IF NOT EXISTS day(key_id INT,inputDate String);");
        database.execSQL(DATABASE_DAY_TRACKER);

        database.execSQL("INSERT INTO day VALUES('0','');");
        database.execSQL("INSERT INTO key VALUES('0');");
        database.execSQL("INSERT INTO counter VALUES('0');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.d("Database_Helper",
                "Upgrading database from " + oldVersion +" to "+newVersion);
        database.execSQL("DROP TABLE IF EXISTS "+ DATABASE_NAME);
        onCreate(database);
    }
}