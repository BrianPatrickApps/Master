package com.example.windows10.work_weather;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("ALL")
public class Main_Room extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,Serializable{
    /**
     * @param db the Database object controlling all the SQLite database
     * @param inputOverlay holds image of the input for users to pick
     * @param rainOverlay holds image of the gif to show rain
     * @param weatherOverlay holds images of the weather corresponding to the median of the room
     */
    protected Database db;
    Button stormy;
    Button rainy;
    Button overcast;
    Button cloudy;
    Button sunny;
    ImageView weatherOverlay;
    ImageView rainOverlay;
    ImageView inputOverlay;
    ButtonController control;
    protected ViewController viewController;
    private boolean sub = false;
    private String idNow;

    /**
     * @param control object that controls the inputs of users to be inserted into the SQLite Databse
     * @param viewController object that controls the images on screen corresponding
     *                      to data from SQLite Database given by users
     */
    ImageView nurse1;
    ImageView nurse2;
    ImageView nurse3;
    ImageView nurse4;
    ImageView nurse5;
    ImageView nurse6;
    ImageView nurse7;
    //    ImageView nurse8;
    ArrayList<ImageView> nurseArray;
    Counter counter;
    /**
     * @param ArrayList holds all the nurse imageviews to be shown when needed
     */

    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        counter = new Counter();
        db = new Database(this);
        Log.d("Main_Room","onCreate() "+"Shift number: "+  +db.getShiftNumber()+ " in Main Activity "+ " Receiver");
        databaseReset();
        databaseReset2();
        databaseReset3();
        //databaseReset4();
        RelativeLayout rel3 = (RelativeLayout)findViewById(R.id.inputScreen);
        @SuppressWarnings("deprecation")
        AbsoluteLayout rel2 = (AbsoluteLayout) findViewById(R.id.Nurse);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.relLay);
        rel.setOnClickListener(tapScreen);

        //Initialize buttons for ButtonController class
        stormy = (Button)findViewById(R.id.Stormy);
        rainy = (Button)findViewById(R.id.Rain);
        overcast = (Button)findViewById(R.id.Overcast);
        cloudy = (Button)findViewById(R.id.Cloudy);
        sunny = (Button)findViewById(R.id.Sunny);
        weatherOverlay = (ImageView)findViewById(R.id.moodOverlay);
        inputOverlay = (ImageView)findViewById(R.id.inputWeather);
        rainOverlay = (ImageView)findViewById(R.id.rainOverlay);


        Glide.with(getApplicationContext()).load(R.drawable.animation_rain).into(rainOverlay);

        viewController = new ViewController(rel,rel2,rel3,rainOverlay,weatherOverlay,inputOverlay);

        control = new ButtonController(stormy,rainy,overcast,cloudy,sunny,inputOverlay,getApplicationContext(),viewController);
        //Makes buttons invisible
        control.setInvisible();

        Glide.with(getApplication().getApplicationContext()).load(R.drawable.animation_rain).into(rainOverlay);

        //checkWeather(db,viewController);
        viewController.startUp();

        nurse1 = (ImageView)findViewById(R.id.nurse1);
        nurse2 = (ImageView)findViewById(R.id.nurse2);
        nurse3 = (ImageView)findViewById(R.id.nurse3);
        nurse4 = (ImageView)findViewById(R.id.nurse4);
        nurse5 = (ImageView)findViewById(R.id.nurse5);
        nurse6 = (ImageView)findViewById(R.id.nurse6);
        nurse7 = (ImageView)findViewById(R.id.nurse7);
        nurseArray = new ArrayList<>();
        nurseArray.add(nurse1);
        nurseArray.add(nurse2);
        nurseArray.add(nurse3);
        nurseArray.add(nurse4);
        nurseArray.add(nurse5);
        nurseArray.add(nurse6);
        nurseArray.add(nurse7);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        db.updateDate(formattedDate);

        View header = navigationView.getHeaderView(0);
        ImageView nursebutton = (ImageView)header.findViewById(R.id.TUNURSE);
        nursebutton.setOnClickListener(nurseMenu);
        clearScreen();


//        ImageView iv = (ImageView)findViewById(R.id.imageView3);
//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewController.fadeOutAndHideImage();
//            }
//        });
    }

    /**
     * Class that controls the objects within the Main_Room activity when needed
     */
    public Main_Room(){
    }

    /**
     * nurseMenu is the menu that is used when Administrator needs to check/change data
     */
    private View.OnClickListener nurseMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
    };

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
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            selectItem(1);
        }
//        else if (id == R.id.nav_fingerprint) {
//            selectItem(2);
//        }
//        else if (id == R.id.nav_data) {
//            selectItem(3);
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *
     * @param position the user selection
     */
    //Which button is to be pressed. Add stuff if need be
    private void selectItem(int position) {

        switch(position) {
            case 1:
                loginID();
                control.setViewable();
                viewController.viewInput();
                break;
            case 2:
                //loginFinger();
                Toast.makeText(getApplicationContext(), "Finger Print Scanner unavailable", Toast.LENGTH_LONG).show();
                break;
            case 3:
                dataLogin();
                break;
            default:
        }
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
                    //int id = Integer.parseInt(input.getText().toString());
                    //if(id > 99999 && id < 1000000)
                    if(input.getText().toString().length() ==6)
                    {
                        control.setViewable();
                        String inputID = input.getText().toString();
                        control.getId(inputID);
                        Log.d("Main_Room","loginID() "+inputID + " InputID");
                        idNow = inputID;
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                viewController.viewNurses();
//                                checkWeather();
//                                inputOverlay.setImageResource(R.drawable.input_1);
//                                showNurses();
//                            }
//                        },5000);//Change to two
//                        showNurses();
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
                Log.d("Main_Room","showNurses() "+"called");
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

    //FingerPrint method no scanner given yet
    @SuppressWarnings("unused")
    private void loginFinger(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Login with Fingerprint");
        alert.setMessage("Finger");

        //an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "Finger Print Scanner unavailable", Toast.LENGTH_LONG).show();
                Intent x =new Intent(Main_Room.this,WeatherRoom.class);
                startActivity(x);
                finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "Back to the menu", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    private void dataLogin(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Admin Login");
        alert.setMessage("Please Enter Password");

        //an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6)});
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    int id = Integer.parseInt(input.getText().toString());
                    if (id == 0) {
                        Intent i = new Intent(Main_Room.this, DataScreen.class);
                        startActivity(i);
                    } else if (id == 1) {
                        Intent i = new Intent(Main_Room.this, WeatherRoom.class);
                        startActivity(i);
                    } else if (id == 2) {
                        db.saveDB();
                    }else if (id == 3) {
                        db.updateShift();
                        Toast.makeText(getApplicationContext(), "Shift has been updated to " + db.getShiftNumber(), Toast.LENGTH_LONG).show();
                        Intent i = new Intent();
                        i.setClass(getApplicationContext(), Main_Room.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().getApplicationContext().startActivity((i));
                        finish();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Sorry wrong password", Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "\t\t\tSorry invalid input\nonly 6 digits are acceptable", Toast.LENGTH_LONG).show();
                    loginID();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "Back to the menu", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    /**
     * checkWeather calculates the median based on the number of nurses that are in the room
     * @param db SQLite controller
     * @param viewController
     */
    protected void checkWeather(Database db,ViewController viewController){
        Double x = db.getRoomMedian();
        Log.d("Main_Room","checkWeather() Check Weather Median is "+ x);
        if(x==0.0){
            viewController.startUp();
        }
        else if(x == 1.0)
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
    }

    public void showNurses(){
        //Available nurse image chosen by counter.
        int reDo =db.doOver(idNow);
        if(reDo == 1)
        {
            changeMind();
        }
        else if(reDo == 0) {
            Log.d("Main_Room","showNurses() new Nurse is being displayed");
            ImageView iv = nurseArray.get(counter.getCount());
            if (counter.getCount() > nurseArray.size())
                counter.resetCount();
            if (!sub) { //boolean check to see if mx number of nurses already visible
                iv.setVisibility(View.VISIBLE);
//                fadeOutAndHideImage(iv);
                Log.d("Main_Room", "showNurses() nurse number: " + iv);
                nurseTimeout(nurseArray.get(counter.getCount()));//calls the nurse timeout method with the imageview of the nurse that just went visible.
                counter.setCount();
                if (counter.getCount() == nurseArray.size()) {
                    counter.removeCount();
                    sub = true;
                }
            } else { //starts setting nurses invisible manually if the max is visible.
                iv.setVisibility(View.GONE);
                counter.removeCount();
                if (counter.getCount() == -1) {
                    counter.setCount();
                    sub = false;
                }
            }
        }
    }

    /**
     * starts a countdown timer that starts when a nurse appears and disappears 2 hours after
     * which then removes the input of the nurse from the current room median and recalculates it
     * @param v
     */
    public void nurseTimeout(View v){
        final ImageView iv = (ImageView) v;
        final String nurseId = control.id();
        new CountDownTimer((1000 * 60 * 120), (1000 * 60 * 120)) { //timer set to be 3 seconds long and tick once every 3 seconds. Will be 2 hours each for final app
//        new CountDownTimer(10000,10000) { //timer set to be 3 seconds long and tick once every 3 seconds. Will be 2 hours each for final app
            public void onTick(long millisUntilFinished) { //nothing needed here as we only disable an image after the full time
            }
            public void onFinish() {
                iv.setVisibility(View.GONE);
                db.changedMind(nurseId);
                checkWeather(db,viewController);
                Log.d("Main_Room","nurseTimeout() "+"Removed");
            }
        }.start();
        Log.d("Main_Room","nurseTimeout() "+"Timer has started for nurseID: "+nurseId);
    }

    public void changeMind(){
        if(counter.getCount() == 0) {
            Log.d("Main_Room","changeMind() "+"Changed mind no nurses "+idNow);
//            counter.removeCount();
            ImageView iv = nurseArray.get(counter.getCount());
            iv.setVisibility(View.VISIBLE);
            counter.setCount();
        }
        else if(counter.getCount() > 0){
            Log.d("Main_Room","changeMind() "+"Changed mine "+ idNow);
            counter.removeCount();
            ImageView imageView = nurseArray.get(counter.getCount());
            Log.d("Main_Room", "changeMind() "+"nurse number: " + imageView);
            imageView.setVisibility(View.GONE);
//            counter.setCount();
            final ImageView iv = nurseArray.get(counter.getCount());
            if (counter.getCount() > nurseArray.size())
                counter.resetCount();
            if (!sub) { //boolean check to see if mx number of nurses already visible
//            iv.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeOutAndHideImage(iv);
                    }
                },1000);
                Log.d("Main_Room", "ChangedMind() nurse number: " + iv + " no Timer has been called");
//                nurseTimeout(nurseArray.get(counter.getCount()));//calls the nurse timeout method with the imageview of the nurse that just went visible.
                counter.setCount();
                if (counter.getCount() == nurseArray.size()) {
                    counter.removeCount();
                    sub = true;
                }
            } else { //starts setting nurses invisible manually if the max is visible.
                iv.setVisibility(View.GONE);
                counter.removeCount();
                if (counter.getCount() == -1) {
                    counter.setCount();
                    sub = false;
                }
            }
        }
    }


    //------------------------------------------------------------------------------------------

    public void databaseReset() {
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
        cal_alarm3.set(Calendar.HOUR_OF_DAY,16);//set the alarm time
        cal_alarm3.set(Calendar.MINUTE, 30);
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
        cal_alarm2.set(Calendar.MINUTE, 30);
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

    @SuppressWarnings("unused")
    public void databaseReset4() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 56);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        Intent myIntent = new Intent(this, MyReceiver4.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 3, myIntent,0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

     void fadeOutAndHideImage(final ImageView img)
    {
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.VISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        img.startAnimation(fadeOut);
    }

    void clearScreen(){
        if(counter.getCount() ==0)
            db.dbClearScreen();
        else
            Log.d("Main_Room","clearScreen() "+"Empty no outlying inputs");
    }

}