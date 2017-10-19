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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static java.lang.Double.parseDouble;

@SuppressWarnings("unused")
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
        Cursor collectedFormattedUsers = database.rawQuery("Select * from nurses WHERE shift_id = '"
                + getShiftNumber()+"' AND inputDate ='"+ getDay()+"';",null);
        ArrayList<String[]>theArray = new ArrayList<>();
        if(collectedFormattedUsers == null){
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
            return theArray;
        }
        else {
            while(collectedFormattedUsers.moveToNext()) {
                String result = collectedFormattedUsers.getString(0) +
                            "/" + collectedFormattedUsers.getString(1) +
                            "/" + collectedFormattedUsers.getString(2) +
                            "/" + collectedFormattedUsers.getString(3);
                String newResult[] = result.split("/");
                theArray.add(newResult);
            }
            Log.d("Database", "collectFormattedUsers() All nurses collected");
            collectedFormattedUsers.close();
            return theArray;
        }
    }

    double getAverage(double mood){
        Cursor roomMedianCursor = database.rawQuery("Select * from nurses WHERE shift_id = '" +
                getShiftNumber()+"' AND inputDate ='"+ getDay()+"';",null);
        ArrayList<Double> collectedDatabaseMean = new ArrayList<>();
        if(roomMedianCursor.getCount() ==0){
            Log.d("Database","getRoomMedian() "+"Empty");
            return mood;
        }
        else {
            while (roomMedianCursor.moveToNext()) {
                Double result = roomMedianCursor.getDouble(1);
                collectedDatabaseMean.add(result);
            }
            collectedDatabaseMean.add(mood);
            Collections.sort(collectedDatabaseMean);
            double median = 0;
            for (Double aCollectedRoomMedian : collectedDatabaseMean) {
                median += aCollectedRoomMedian;
            }
            median = median/collectedDatabaseMean.size();
            Log.d("Database", "getAverage() " + collectedDatabaseMean.size()
                    + " size of the sample size, " + "Cursor size: " + roomMedianCursor.getCount());
            roomMedianCursor.close();
            DecimalFormat df = new DecimalFormat("#.##");
            return parseDouble(df.format(median));
        }
    }

    double getRoomMedian(){
        Cursor roomMedianCursor = database.rawQuery("Select * from nurses WHERE shift_id = '"
                + getShiftNumber()+"' AND inputDate ='"+ getDay()+"';",null);
        ArrayList<Double> collectedRoomMean = new ArrayList<>();
        if(roomMedianCursor.getCount() ==0){
            Log.d("Database","getRoomMedian() "+"Empty");
            return 0;
        }
        else {
            while (roomMedianCursor.moveToNext()) {
                Double result = roomMedianCursor.getDouble(1);
                collectedRoomMean.add(result);
            }
            Collections.sort(collectedRoomMean);
            double median = 0;
            for (Double aCollectedRoomMedian : collectedRoomMean) {
                median += aCollectedRoomMedian;
            }
            median = median/collectedRoomMean.size();
            Log.d("Database", "getRoomMedian() " + collectedRoomMean.size()
                    + " size of the sample size, " + "Cursor size: " + roomMedianCursor.getCount());
            roomMedianCursor.close();
            DecimalFormat df = new DecimalFormat("#.##");
            return parseDouble(df.format(median));
        }
    }

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
        Cursor shiftNumberMean = database.rawQuery("Select * from key;",null);
        ArrayList<Integer>collectedShiftNumbers = new ArrayList<>();
        if(shiftNumberMean.getCount() ==0){
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }
        while(shiftNumberMean.moveToNext()){
            int result = shiftNumberMean.getInt(0);
            collectedShiftNumbers.add(result);
        }
        shiftNumberMean.close();
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
        if(getCountNumberCursor.getCount() ==0)
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();

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
        if(getDayCursor.getCount() ==0)
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();

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
                String csvStrings[] = {csvCursor.getString(0),
                        csvCursor.getString(1), csvCursor.getString(2),
                        csvCursor.getString(3), csvCursor.getString(4),csvCursor.getString(5)};
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
    }

     void changedMind(String id){
        database.execSQL("UPDATE nurses set changed = '"+ 1 + "' where id= '"+
                id +"'AND inputDate ='"+ getDay()+ "' AND shift_id ='"+ getShiftNumber() +"';");
        Log.d("Database","changedMind() "+"changedMind is called");
    }

    void dbClearScreen(){
        Cursor dbClearScreenCursor = database.rawQuery("Select * from nurses",null);
        if(dbClearScreenCursor.getCount() > 0) {
            database.execSQL("DELETE FROM nurses");
            Log.d("Main_Room","Cleared");
        }
        dbClearScreenCursor.close();
    }
     void closeDatabase(){
        database.close();
     }
}