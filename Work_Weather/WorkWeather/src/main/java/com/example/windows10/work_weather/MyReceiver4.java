package com.example.windows10.work_weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver4 extends BroadcastReceiver{
    private static final String TAG = "Receiver";
    @Override
    public void onReceive(Context context, Intent in) {
        Database db = new Database(context);
        db.setShift(0);
//        db.saveDB();
        Log.d(TAG,"Shift number gone up "+  +db.getShiftNumber()+"Receiver 4 has gone off");
        Intent i = new Intent();
        i.setClass(context, Main_Room.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity((i));
    }
}
