package com.example.windows10.work_weather;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main_Room extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,Serializable{
    /**
     * @param db the Database object controlling all the SQLite database
     * @param inputOverlay holds image of the input for users to pick
     * @param rainOverlay holds image of the gif to show rain
     * @param weatherOverlay holds images of the weather corresponding to the median of the room
     */

    private Database db;
    private ImageView inputOverlay;
    private ButtonController control;
    private ViewController viewController;
    private boolean sub = false;
    private boolean notUsedNotification = false;
    private CountDownTimer notifyUsers;
    private String idNow;

//    CountDownTimer timer1;
//    CountDownTimer timer2;
//    CountDownTimer timer3;
//    CountDownTimer timer4;
//    CountDownTimer timer5;
//    CountDownTimer timer6;
//    CountDownTimer timer7;
//
    private NurseTimer nurseTimer1;
    private NurseTimer nurseTimer2;
    private NurseTimer nurseTimer3;
    private NurseTimer nurseTimer4;
    private NurseTimer nurseTimer5;
    private NurseTimer nurseTimer6;
    private NurseTimer nurseTimer7;

    /**
     * @param control object that controls the inputs of users to be inserted into the SQLite Databse
     * @param viewController object that controls the images on screen corresponding
     *                      to data from SQLite Database given by users
     */
    private ArrayList<ImageView> nurseArray;
    private Counter counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        counter = new Counter();
        db = new Database(this);
        Log.d("Main_Room","App has started, the shift number is " + db.getShiftNumber());

        databaseReset();
        databaseReset2();
        databaseReset3();

        RelativeLayout rel3 = (RelativeLayout)findViewById(R.id.inputScreen);
        @SuppressWarnings("deprecation")
        AbsoluteLayout rel2 = (AbsoluteLayout) findViewById(R.id.Nurse);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*
      */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.relLay);
        rel.setOnClickListener(tapScreen);

        //Initialize buttons for ButtonController class
        Button stormy = (Button) findViewById(R.id.Stormy);
        Button rainy = (Button) findViewById(R.id.Rain);
        Button overcast = (Button) findViewById(R.id.Overcast);
        Button cloudy = (Button) findViewById(R.id.Cloudy);
        Button sunny = (Button) findViewById(R.id.Sunny);

        ImageView weatherOverlay = (ImageView) findViewById(R.id.moodOverlay);
        inputOverlay = (ImageView)findViewById(R.id.inputWeather);
        ImageView rainOverlay = (ImageView) findViewById(R.id.rainOverlay);

        viewController = new ViewController(rel,rel2,rel3, rainOverlay, weatherOverlay,inputOverlay);

        control = new ButtonController(stormy, rainy, overcast, cloudy, sunny,inputOverlay,getApplicationContext(),viewController);
        //Makes buttons invisible
        control.setInvisible();
        Glide.with(getApplication().getApplicationContext()).load(R.drawable.animation_rain).into(rainOverlay);
        viewController.startUp();

        ImageView nurse1 = (ImageView)findViewById(R.id.nurse1);
        ImageView nurse2 = (ImageView)findViewById(R.id.nurse2);
        ImageView nurse3 = (ImageView)findViewById(R.id.nurse3);
        ImageView nurse4 = (ImageView)findViewById(R.id.nurse4);
        ImageView nurse5 = (ImageView)findViewById(R.id.nurse5);
        ImageView nurse6 = (ImageView)findViewById(R.id.nurse6);
        ImageView nurse7 = (ImageView)findViewById(R.id.nurse7);
        nurseArray = new ArrayList<>();
        nurseArray.add(nurse1);
        nurseArray.add(nurse2);
        nurseArray.add(nurse3);
        nurseArray.add(nurse4);
        nurseArray.add(nurse5);
        nurseArray.add(nurse6);
        nurseArray.add(nurse7);

        Calendar c = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        db.updateDate(formattedDate);

        View header = navigationView.getHeaderView(0);
        ImageView nursebutton = (ImageView)header.findViewById(R.id.TUNURSE);
        nursebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataLogin();
            }
        });
        clearScreen();

    }

    /**
     * Class that controls the objects within the Main_Room activity when needed
     */
    public Main_Room(){
    }

    /**
     * nurseMenu is the menu that is used when Administrator needs to check/change data
     */
    private void nurseMenu(){
            final String[] option = {"Data","Test","Save","Change"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Main_Room.this,android.R.layout.select_dialog_item,option);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Main_Room.this, R.style.AlertDialogCustom));
            builder.setTitle("Please Select");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0){
                        Intent i = new Intent(Main_Room.this, WeatherRoom.class);
                        startActivity(i);
                    }
                    else if (which ==1){
                        Intent i = new Intent(Main_Room.this, DataScreen.class);
                        startActivity(i);
                    }
                    else if (which ==2){
                        db.saveDB();
                        Toast.makeText(getApplicationContext(), "DB Saved", Toast.LENGTH_LONG).show();
                    }
                    else if (which ==3){
                        db.updateShift();
                        Toast.makeText(getApplicationContext(), "Shift has been updated to " + db.getShiftNumber(), Toast.LENGTH_LONG).show();
                        Intent i = new Intent();
                        i.setClass(getApplicationContext(), Main_Room.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().getApplicationContext().startActivity((i));
                        finish();
                    }
                }
            });
            builder.show();
        }

    //When back is pressed, not used
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //When screen is tapped opens drawer
    private View.OnClickListener tapScreen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    }  ;

    //Drawer Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main__room, menu);
        return true;
    }
    //Unused drawer method for settings
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * onNavigationItemSelected selects the method to execute from user input
     * @param item selected object
     * @return true
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            selectItem();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Which button is to be pressed. Add stuff if need be
    private void selectItem() {

        switch(1) {
            case 1:
                loginID();
                control.setViewable();
                viewController.viewInput();
                break;
            default:
        }
    }

    private void dataLogin() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Admin Login");
        alert.setMessage("Please Enter Password");

        //an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    int id = Integer.parseInt(input.getText().toString());
                    if (id == 0) {
                        nurseMenu();
                    } else
                        Toast.makeText(getApplicationContext(), "Sorry wrong password", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "\t\t\tSorry invalid input", Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.show();
    }

    //Login alertDialog box
    /**
     * loginID method that collects the 6 digit ID of the user and sends it to the ButtonController,
     * which INSERTS the data of the user into the SQLite Database.
     * After inserting a nurse ImageView appears that corresponds to the user, the median is recalculated
     * based on the number of nurses in the room or in the database
     */
    private void loginID(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Login with ID");
        alert.setMessage("Please use your 6 digit code");
        alert.setCancelable(false);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6)});
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                try {
                    if(input.getText().toString().length() ==6)
                    {
                        control.setViewable();
                        String inputID = input.getText().toString();
                        control.getId(inputID);
                        idNow = inputID;
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Must be 6 digits", Toast.LENGTH_SHORT).show();
                        loginID();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "\t\t\tSorry invalid input\nonly 6 digits are acceptable", Toast.LENGTH_LONG).show();
                    loginID();
                }
                showNurses();
                if (!notUsedNotification) {
                    unusedNotification();
                }
                else {
                    notifyUsers.cancel();
                    Log.d("Main_Room","unusedNotification cancelled");
                }

            }
        });
        //When they cancel
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "Back to the menu ", Toast.LENGTH_SHORT).show();
                control.setInvisible();
                viewController.viewNurses();
                checkWeather(db,viewController);
                inputOverlay.setImageResource(R.drawable.input_1);
            }
        });
        alert.show();
    }

    /**
     * checkWeather calculates the median based on the number of nurses that are in the room
     * @param db SQLite controller
     * @param viewController controls the images
     */
    void checkWeather(Database db, ViewController viewController){
        Double x = db.getRoomMedian();
        Log.d("Main_Room","Recalculation of the median is: "+ x);
        if (x != 0.0) {
            if(x == 1.0)
            {
                viewController.showThunder();
            }
            else if(x ==2.0 || x == 1.5){
                viewController.showRainMood();
            }
            else if(x ==3.0|| x == 2.5)
            {
                viewController.stopRain();
                viewController.showOvercast();
            }
            else if(x ==4.0|| x == 3.5){
                viewController.stopRain();
                viewController.showClouds();
            }
            else if(x==5.0|| x == 4.5) {
                viewController.stopRain();
                viewController.showSun();
            }
        } else {
            viewController.startUp();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showNurses(){
        //Available nurse image chosen by counter.
        int reDo =db.doOver(idNow);
        if(reDo == 1)
            changeMind();
        else if(reDo == 0) {
            Log.d("Main_Room","new Nurse is being displayed");
            ImageView nurseView = nurseArray.get(counter.getCount());
            if (!sub) { //boolean check to see if mx number of nurses already visible
                nurseView.setVisibility(View.VISIBLE);
                setTimer(nurseView,counter.getCount());
                getTimer(counter.getCount()).startTimer();
                counter.setCount();
                if (counter.getCount() == nurseArray.size()) {
                    sub = true;
                    counter.resetCount();
                }
            }
            else { //starts setting nurses invisible manually if the max is visible.
                NurseTimer maxedOut = getTimer(counter.getCount());
                ImageView previousNurse = maxedOut.getNurseImage();
                previousNurse.setVisibility(View.GONE);
                if (maxedOut != null) {
                    maxedOut.maxedReached();
                }
                ImageView newNurse = nurseArray.get(counter.getCount());
                newNurse.setVisibility(View.VISIBLE);
                setTimer(newNurse,counter.getCount());
                getTimer(counter.getCount()).startTimer();
                counter.setCount();
                Log.d("Main_Room","Nurse Array has gone over 7 and is now in the reset loop");
                if (counter.getCount() == 7) {
                    counter.resetCount();
                    sub = false;
                    Log.d("Main_Room","NurseArray has been reset");
                }
            }
        }
    }

    private void changeMind(){
        if(counter.getCount() == 0) {
            Log.d("Main_Room","This ID is already in the Database but the room is empty: "+idNow);
            ImageView nurseView = nurseArray.get(counter.getCount());
            nurseView.setVisibility(View.VISIBLE);
            counter.setCount();
        }
        else if(counter.getCount() > 0){
            counter.removeCount();
            ImageView nurseView = nurseArray.get(counter.getCount());
            nurseView.setVisibility(View.GONE);
            Log.d("Main_Room","This ID is already in the Database no timer will be called: "+ idNow);
            final ImageView nurseImage = nurseArray.get(counter.getCount());
            if (!sub) { //boolean check to see if mx number of nurses already visible
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeOutAndHideImage(nurseImage);
                    }
                },1000);
                counter.setCount();
                if (counter.getCount() == nurseArray.size()) {
                    counter.resetCount();
                    sub = true;
                }
            } else { //starts setting nurses invisible manually if the max is visible.
                NurseTimer maxedOut = getTimer(counter.getCount());
                ImageView previousNurse = maxedOut.getNurseImage();
                previousNurse.setVisibility(View.GONE);
                if (maxedOut != null) {
                    maxedOut.maxedReached();
                }
                ImageView newNurse = nurseArray.get(counter.getCount());
                setTimer(newNurse,counter.getCount());
                getTimer(counter.getCount()).startTimer();
                counter.setCount();
                newNurse.setVisibility(View.VISIBLE);
                Log.d("Main_Room","Nurse Array has gone over 7 and is now in the reset loop");
                if (counter.getCount() == 7) {
                    counter.resetCount();
                    sub = false;
                    Log.d("Main_Room","NurseArray has been reset");
                }
            }
        }
    }

    private void unusedNotification(){

        notUsedNotification = true;
        Log.d("Main_Room","unusedNotification has started: "+ true);
        notifyUsers =new CountDownTimer((1000 * 60 * 20), (1000 * 60 * 20)) {
//        notifyUsers =new CountDownTimer((10000), (10000)) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                viewController.viewInput();
                notUsedNotification = false;
                final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relLay);
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewController.viewNurses();
                        relativeLayout.setOnClickListener(tapScreen);
                        Log.d("Main_Room","unusedNotification has finished"+ notUsedNotification);
                    }
                });
            }
        }.start();
    }

    private void clearScreen(){
        if(counter.getCount() ==0)
            db.dbClearScreen();
        else
            Log.d("Main_Room","clearScreen() "+"There are no outlying inputs");
    }

    private void fadeOutAndHideImage(final ImageView img) {
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation) {img.setVisibility(View.VISIBLE);}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        img.startAnimation(fadeOut);
    }

    //------------------------------------------------------------------------------------------

    private void databaseReset() {
        Toast.makeText(getApplicationContext(), "Alarm 1 set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //set time for 22:00 reset
        Date dat3  = new Date();
        Calendar cal_alarm3 = Calendar.getInstance();
        Calendar cal_now3 = Calendar.getInstance();
        cal_now3.setTime(dat3);
        cal_alarm3.setTime(dat3);
        cal_alarm3.set(Calendar.HOUR_OF_DAY,15);//set the alarm time
        cal_alarm3.set(Calendar.MINUTE, 0);
        cal_alarm3.set(Calendar.SECOND,0);
        if(cal_alarm3.before(cal_now3)){//if its in the past increment
            cal_alarm3.add(Calendar.DATE,1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm3.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void databaseReset2() {
        Toast.makeText(getApplicationContext(), "Alarm 2 set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MyReceiver2.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //set time for 16:00 reset
        Date dat2  = new Date();//initializes to now
        Calendar cal_alarm2 = Calendar.getInstance();
        Calendar cal_now2 = Calendar.getInstance();
        cal_now2.setTime(dat2);
        cal_alarm2.setTime(dat2);
        cal_alarm2.set(Calendar.HOUR_OF_DAY,22);//set the alarm time
        cal_alarm2.set(Calendar.MINUTE, 45);
        cal_alarm2.set(Calendar.SECOND,0);
        if(cal_alarm2.before(cal_now2)){//if its in the past increment
            cal_alarm2.add(Calendar.DATE,1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm2.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void databaseReset3() {
        Toast.makeText(getApplicationContext(), "Alarm 3 set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MyReceiver3.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),4, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //set time for 16:00 reset
        Date dat2  = new Date();//initializes to now
        Calendar cal_alarm2 = Calendar.getInstance();
        Calendar cal_now2 = Calendar.getInstance();
        cal_now2.setTime(dat2);
        cal_alarm2.setTime(dat2);
        cal_alarm2.set(Calendar.HOUR_OF_DAY,7);//set the alarm time
        cal_alarm2.set(Calendar.MINUTE, 30);
        cal_alarm2.set(Calendar.SECOND,0);
        if(cal_alarm2.before(cal_now2)){//if its in the past increment
            cal_alarm2.add(Calendar.DATE,1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm2.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void setTimer(View v,int countDownID) {
        Log.d("Main_Room","setTimer is starting: " + countDownID);

        if(countDownID ==0) {
            final ImageView iv = (ImageView) v;
            final String nurseId = control.id();
            nurseTimer1 = new NurseTimer(iv,nurseId,this,viewController);
        }
        else if (countDownID ==1){
            final ImageView iv = (ImageView) v;
            final String nurseId = control.id();
            nurseTimer2 = new NurseTimer(iv,nurseId,this,viewController);
        }
        else if (countDownID ==2){
            final ImageView iv = (ImageView) v;
            final String nurseId = control.id();
            nurseTimer3 = new NurseTimer(iv,nurseId,this,viewController);
        }
        else if (countDownID ==3){
            final ImageView iv = (ImageView) v;
            final String nurseId = control.id();
            nurseTimer4 = new NurseTimer(iv,nurseId,this,viewController);
        }
        else if (countDownID ==4){
            final ImageView iv = (ImageView) v;
            final String nurseId = control.id();
            nurseTimer5 = new NurseTimer(iv,nurseId,this,viewController);
        }
        else if (countDownID ==5){
            final ImageView iv = (ImageView) v;
            final String nurseId = control.id();
            nurseTimer6 = new NurseTimer(iv,nurseId,this,viewController);
        }
        else if (countDownID ==6){
            final ImageView iv = (ImageView) v;
            final String nurseId = control.id();
            nurseTimer7 = new NurseTimer(iv,nurseId,this,viewController);
        }
    }

    private NurseTimer getTimer(int timerID){
        Log.d("Main_Room","timerID: " + timerID+ " has been called.");
        switch (timerID){
            case 0:
                return nurseTimer1;
            case 1:
                return nurseTimer2;
            case 2:
                return nurseTimer3;
            case 3:
                return nurseTimer4;
            case 4:
                return nurseTimer5;
            case 5:
                return nurseTimer6;
            case 6:
                return nurseTimer7;
            default:
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

}