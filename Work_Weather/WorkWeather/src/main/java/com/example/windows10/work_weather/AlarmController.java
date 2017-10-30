package com.example.windows10.work_weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

class AlarmController {

    private Context context;

    AlarmController(Context context){
        this.context = context;
    }

    void startAlarms(){
        databaseReset();
        databaseReset2();
        databaseReset3();
        Log.d("Main_Room","Alarms have started");
    }

    private void databaseReset() {
        Toast.makeText(context, "Alarm 1 set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Date date  = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(date);
        cal_alarm.setTime(date);
        cal_alarm.set(Calendar.HOUR_OF_DAY,15);
        cal_alarm.set(Calendar.MINUTE, 0);
        cal_alarm.set(Calendar.SECOND,0);
        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void databaseReset2() {
        Toast.makeText(context, "Alarm 2 set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, MyReceiver2.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Date date2  = new Date();
        Calendar cal_alarm2 = Calendar.getInstance();
        Calendar cal_now2 = Calendar.getInstance();
        cal_now2.setTime(date2);
        cal_alarm2.setTime(date2);
        cal_alarm2.set(Calendar.HOUR_OF_DAY,22);
        cal_alarm2.set(Calendar.MINUTE, 45);
        cal_alarm2.set(Calendar.SECOND,0);
        if(cal_alarm2.before(cal_now2)){
            cal_alarm2.add(Calendar.DATE,1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm2.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void databaseReset3() {
        Toast.makeText(context, "Alarm 3 set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, MyReceiver3.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,4, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Date date3  = new Date();
        Calendar cal_alarm3 = Calendar.getInstance();
        Calendar cal_now3 = Calendar.getInstance();
        cal_now3.setTime(date3);
        cal_alarm3.setTime(date3);
        cal_alarm3.set(Calendar.HOUR_OF_DAY,7);
        cal_alarm3.set(Calendar.MINUTE, 30);
        cal_alarm3.set(Calendar.SECOND,0);
        if(cal_alarm3.before(cal_now3)){
            cal_alarm3.add(Calendar.DATE,1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm3.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}
