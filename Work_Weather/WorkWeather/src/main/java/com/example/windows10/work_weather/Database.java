package com.example.windows10.work_weather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

class Database implements Serializable{

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    Database(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    void execSQL(String query) {
        database.execSQL(query);
    }

    ArrayList<String[]> collectFormattedUsers(){
        Cursor collectedFormattedUsers = database.rawQuery("Select * from nurses WHERE shift_id = '"+ getShiftNumber()+"' AND inputDate ='"+ getDay()+"';",null);
        ArrayList<String[]>theArray = new ArrayList<>();
        if(collectedFormattedUsers == null){
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
            return theArray;
        }
        else {
            while (collectedFormattedUsers.moveToNext()) {
                String result = collectedFormattedUsers.getString(0) +
                            "/" + collectedFormattedUsers.getString(1) +
                            "/" + collectedFormattedUsers.getString(2) +
                            "/" + collectedFormattedUsers.getString(3) +
                            "/" + collectedFormattedUsers.getString(4);
                String newResult[] = result.split("/");
                theArray.add(newResult);
            }
            Log.d("Database", "collectFormattedUsers() All nurses collected");
            collectedFormattedUsers.close();
            return theArray;
        }
    }

    //Gets the median
    double getAverage(double mood){
        Cursor roomMedianCursor = database.rawQuery("Select * from nurses WHERE shift_id = '"+ getShiftNumber()+"' AND inputDate ='"+ getDay()+
                "' AND changed ='"+ 0 +"';",null);
        ArrayList<Double> collectedRoomMedian = new ArrayList<>();
        if(roomMedianCursor.getCount() ==0){
            Log.d("Database","getRoomMedian() "+"Empty");
            return mood;
        }
        else {
            while (roomMedianCursor.moveToNext()) {
                Double result = roomMedianCursor.getDouble(1);
                collectedRoomMedian.add(result);
            }
            collectedRoomMedian.add(mood);
            Collections.sort(collectedRoomMedian);
            double median;
            if (collectedRoomMedian.size() % 2 == 0) {
                median = (collectedRoomMedian.get(collectedRoomMedian.size() / 2) + collectedRoomMedian.get(collectedRoomMedian.size() / 2 - 1)) / 2;
            } else {
                median = collectedRoomMedian.get(collectedRoomMedian.size() / 2);
            }
            Log.d("Database", "getAverage() " + collectedRoomMedian.size() + " size of the sample size, " + "Cursor size: " + roomMedianCursor.getCount());
            roomMedianCursor.close();
            return median;
        }
    }

    double getRoomMedian(){
        Cursor roomMedianCursor = database.rawQuery("Select * from nurses WHERE shift_id = '"+ getShiftNumber()+"' AND inputDate ='"+ getDay()+
                "' AND changed ='"+ 0 +"';",null);
        ArrayList<Double> collectedRoomMedian = new ArrayList<>();
        if(roomMedianCursor.getCount() ==0){
            Log.d("Database","getRoomMedian() "+"Empty");
            return 0;
        }
        else {
            while (roomMedianCursor.moveToNext()) {
                Double result = roomMedianCursor.getDouble(1);
                collectedRoomMedian.add(result);
            }
            Collections.sort(collectedRoomMedian);
            double median;
            if (collectedRoomMedian.size() % 2 == 0) {
                median = (collectedRoomMedian.get(collectedRoomMedian.size() / 2) + collectedRoomMedian.get(collectedRoomMedian.size() / 2 - 1)) / 2;
            } else {
                median = collectedRoomMedian.get(collectedRoomMedian.size() / 2);
            }
            Log.d("Database", "getRoomMedian() " + collectedRoomMedian.size() + " size of the sample size, " + "Cursor size: " + roomMedianCursor.getCount());
            roomMedianCursor.close();
            return median;
        }
    }

    //Adds Median to avgShift and avgRoom
    void addMedian(double median, String date, int shift){
        String query = "INSERT into avgShift(`shift_id`,`average`,`inputDate`)" +
                "VALUES('" + shift + "','"+ median +"','"+ date +"');";
        database.execSQL(query);
        String updateMedian = "UPDATE avgRoom set median = '"+ median +"',inputDate='"+ getDay()+"' WHERE key_id = '"+getShiftNumber()+"';";
        database.execSQL(updateMedian);
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();
    }

    //Collects the median of the shift
    double getMedian(){
        ArrayList<Double> collectedMedians = new ArrayList<>();
        Cursor getMedianCursor = database.rawQuery("Select * from avgRoom where key_id = '"+getShiftNumber()+
                "'AND inputDate ='"+ getDay()+ "';",null);
        if(getMedianCursor.getCount() ==0)
            return 0.0;
        else{
            while(getMedianCursor.moveToNext()) {
                Double median = getMedianCursor.getDouble(1);
                collectedMedians.add(median);
            }
        }
        getMedianCursor.close();
        Log.d("Database",collectedMedians.get(0)+ "is the Median");
        return collectedMedians.get(0);
    }

    //gets called when the broadcast receiver fires
    void updateShift(){
        String query = "UPDATE key set key_id = '"+(getShiftNumber()+1)+"' WHERE key_id ='"+getShiftNumber()+"';";
        execSQL(query);
        resetKey();
        Log.d("Database","updateShift() "+"Shift number has been updated " + getShiftNumber());
    }

    void setShift(int number){
        String query = "UPDATE key set key_id = '"+ number +"' WHERE key_id ='"+getShiftNumber()+"';";
        resetKey();
        execSQL(query);
        Log.d("Database","setShift() "+"Shift Number has been updated: "+ getShiftNumber());
    }

    int getShiftNumber(){
        Cursor shiftNumberMedian = database.rawQuery("Select * from key;",null);
        ArrayList<Integer>collectedShiftNumbers = new ArrayList<>();
        if(shiftNumberMedian.getCount() ==0){
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }
        while(shiftNumberMedian.moveToNext()){
            int result = shiftNumberMedian.getInt(0);
            collectedShiftNumbers.add(result);
        }
        shiftNumberMedian.close();
        return collectedShiftNumbers.get(0);
    }

    private void resetKey(){
        Cursor resetKeyCursors = database.rawQuery("Select * from key;",null);
        ArrayList<Integer>collectedKey = new ArrayList<>();
        if(resetKeyCursors.getCount() ==0){
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }
        while(resetKeyCursors.moveToNext()){
            int result = resetKeyCursors.getInt(0);
            collectedKey.add(result);
        }
        int key = collectedKey.get(0);
        String query = "UPDATE key set key_id = '"+0+"' WHERE key_id ='"+getShiftNumber()+"';";
        if(key ==4) {
            execSQL(query);
            Log.d("Database","resetKey() "+"Shift Number Reset to: " + getShiftNumber());
        }
        resetKeyCursors.close();
    }

    private int getCountNumber(){
        Cursor getCountNumberCursor = database.rawQuery("Select * from counter;",null);
        ArrayList<Integer>collectedCountNumber = new ArrayList<>();
        if(getCountNumberCursor.getCount() ==0){
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }
        while(getCountNumberCursor.moveToNext()){
            int result = getCountNumberCursor.getInt(0);
            collectedCountNumber.add(result);
        }
        getCountNumberCursor.close();
        return collectedCountNumber.get(0);
    }

    String getDay(){
        Cursor getDayCursor = database.rawQuery("Select * from day;",null);
        ArrayList<String>collectedDay = new ArrayList<>();
        if(getDayCursor.getCount() ==0){
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }
        while(getDayCursor.moveToNext()){
            String result = getDayCursor.getString(1);
            collectedDay.add(result);
        }
        getDayCursor.close();
        return collectedDay.get(0);
    }

    void updateDate(String newDate){
        String query = "UPDATE day set inputDate = '"+newDate+"' WHERE key_id ='"+0+"';";
        Log.d("Database","updateDate() "+"Date has been updated: "+ getDay());
        resetKey();
        execSQL(query);
    }

    void saveDB() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        File exportDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"WorkWeather");
        if (!exportDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            exportDirectory.mkdirs();
        }
        Log.d("Database","saveDB() "+exportDirectory.toString());
        File file = new File(exportDirectory, currentDateTimeString+ " " +getCountNumber()+ ".csv");
        Log.d("Database","saveDB() "+file.toString());
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor csvCursor = db.rawQuery("SELECT * FROM nurses", null);
            csvWriter.writeNext(csvCursor.getColumnNames());
            while (csvCursor.moveToNext()) {
                String csvStrings[] = {csvCursor.getString(0), csvCursor.getString(1), csvCursor.getString(2), csvCursor.getString(3), csvCursor.getString(4),csvCursor.getString(5)};
                csvWriter.writeNext(csvStrings);
                Log.d("Database","saveDB() "+csvStrings[0]);
            }
            csvWriter.close();
            csvCursor.close();
            MediaScannerConnection.scanFile(context, new String[] {exportDirectory.toString()}, null, null);
            String query2 = "UPDATE counter set key_id = '"+(getCountNumber()+1)+"' WHERE key_id ='"+getCountNumber()+"';";
            execSQL(query2);
            Log.d("Database","saveDB() "+"Counter has been updated: "+ getCountNumber());
        }
        catch (Exception sqlEx) {
            Log.d("Database", "saveDB() "+sqlEx.getMessage()+ "Exception", sqlEx);
        }
        saveDBMedian();
    }

    void saveDBMedian() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        File exportDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"WorkWeather");
        if (!exportDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            exportDirectory.mkdirs();
        }
        Log.d("Database","saveDB() "+exportDirectory.toString());
        File file = new File(exportDirectory, currentDateTimeString+ " " +getCountNumber()+" Medians" +".csv");
        Log.d("Database","saveDB() "+file.toString());
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor csvCursor = db.rawQuery("SELECT * FROM avgShift", null);
            csvWriter.writeNext(csvCursor.getColumnNames());
            while (csvCursor.moveToNext()) {
                String csvStrings[] = {csvCursor.getString(0), csvCursor.getString(1), csvCursor.getString(2)};
                csvWriter.writeNext(csvStrings);
                Log.d("Database","saveDB() "+csvStrings[0]);
            }
            csvWriter.close();
            csvCursor.close();
            MediaScannerConnection.scanFile(context, new String[] {exportDirectory.toString()}, null, null);
        }
        catch (Exception sqlEx) {
            Log.d("Database", "saveDB() "+sqlEx.getMessage()+ "Exception", sqlEx);
        }
    }

    int factCheck(String id){
        int factCheck;
        Cursor factCheckCursor = database.rawQuery("Select COUNT(id) from nurses where id = '"+id+"' AND inputDate ='"+
                getDay()+ "' AND shift_id ='"+ getShiftNumber() +"';",null);
        ArrayList<Integer>collectedInput = new ArrayList<>();
        while(factCheckCursor.moveToNext()){
            int a = factCheckCursor.getInt(0);
            collectedInput.add(a);
        }
        if(collectedInput.get(0) > 0)
            factCheck =1;
        else
            factCheck =0;
        factCheckCursor.close();
        Log.d("Database","factCheck() "+"factCheck output is "+ factCheck+ ", There is already: "+ collectedInput.get(0) + " ID is " + id);
        return factCheck;
    }

     void changedMind(String id){
        database.execSQL("UPDATE nurses set changed = '"+ 1 + "' where id= '"+
                id +"'AND inputDate ='"+ getDay()+ "' AND shift_id ='"+ getShiftNumber() +"';");
        Log.d("Database","changedMind() "+"changedMind is called");
    }

    void dbClearScreen(){
        Cursor dbClearScreenCursor =database.rawQuery("Select id from nurses where changed = '"+0+"'AND inputDate ='"+ getDay()+ "' AND shift_id ='"+ getShiftNumber() +"';",null);
        Log.d("Database", "dbClearScreen() "+"Size of cursor:" + dbClearScreenCursor.getCount());
        ArrayList<Integer> previousRoomNurses = new ArrayList<>();
        while (dbClearScreenCursor.moveToNext()){
            previousRoomNurses.add(dbClearScreenCursor.getInt(0));
        }
        dbClearScreenCursor.close();
        for(int i=0;i<previousRoomNurses.size();i++) {
            Log.d("Database","dbClearScreen() "+"aList:"+ i +" id is: " +previousRoomNurses.get(i));
            database.execSQL("UPDATE nurses set changed = '"+ 1 + "' where id= '"+previousRoomNurses.get(i)
                    +"'AND inputDate ='"+ getDay()+ "' AND shift_id ='"+ getShiftNumber() +"';");
            Log.d("Database","dbClearScreen() "+"UPDATE nurses set changed = '"+
                    1 + "' where id= '"+previousRoomNurses.get(i)+"'AND inputDate ='"+ getDay()+ "' AND shift_id ='"+ getShiftNumber() +"';");
        }
    }
     void closeDatabase(){
        database.close();
     }
}