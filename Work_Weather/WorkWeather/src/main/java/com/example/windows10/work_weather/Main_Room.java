package com.example.windows10.work_weather;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main_Room extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,Serializable{

    private Database database;
    private ViewController viewController;
    private boolean sub = false;
    private boolean notUsedNotification = false;
    private CountDownTimer notifyUsers;
    private String idNow;

    private NurseTimer nurseTimer1;
    private NurseTimer nurseTimer2;
    private NurseTimer nurseTimer3;
    private NurseTimer nurseTimer4;
    private NurseTimer nurseTimer5;
    private NurseTimer nurseTimer6;
    private NurseTimer nurseTimer7;

    private ArrayList<ImageView> nurseArray;
    private Counter counter;
    private Map<String,Integer> nurseMap;

    private Button stormy;
    private Button rainy;
    private Button overcast;
    private Button cloudy;
    private Button sunny;
    private ImageView inputOverlay;
    private Double mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        counter = new Counter();
        database = new Database(this);
        Log.d("Main_Room","App has started, the shift number is " + database.getShiftNumber());

        AlarmController alarmController = new AlarmController(this);
        alarmController.startAlarms();
        RelativeLayout rel3 = (RelativeLayout)findViewById(R.id.inputScreen);
        AbsoluteLayout rel2 = (AbsoluteLayout) findViewById(R.id.Nurse);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView weatherOverlay = (ImageView) findViewById(R.id.moodOverlay);
        ImageView rainOverlay = (ImageView) findViewById(R.id.rainOverlay);
        inputOverlay = (ImageView) findViewById(R.id.inputWeather);

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.relLay);
        rel.setOnClickListener(tapScreen);
        
        stormy = (Button) findViewById(R.id.Stormy);
        rainy = (Button) findViewById(R.id.Rain);
        overcast = (Button) findViewById(R.id.Overcast);
        cloudy = (Button) findViewById(R.id.Cloudy);
        sunny = (Button) findViewById(R.id.Sunny);

        stormy.setOnClickListener(stormyClicked);
        rainy.setOnClickListener(rainyClicked);
        overcast.setOnClickListener(overcastClicked);
        cloudy.setOnClickListener(cloudyClicked);
        sunny.setOnClickListener(sunnyClicked);
        
        viewController = new ViewController(rel,rel2,rel3, rainOverlay, weatherOverlay, inputOverlay);
        viewController.startUp();
        setInvisible();

        Glide.with(getApplication().getApplicationContext()).load(R.drawable.animation_rain).into(rainOverlay);

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
        nurseMap = new HashMap<>();
        Calendar c = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        database.updateDate(formattedDate);

        View header = navigationView.getHeaderView(0);
        ImageView nurseButton = (ImageView)header.findViewById(R.id.TUNURSE);
        nurseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataLogin();
            }
        });
        clearScreen();
        setNurseTimers();
    }

    private void nurseMenu(){
            final String[] option = {"Data","Test","Save","Change"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Main_Room.this,android.R.layout.select_dialog_item,option);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Main_Room.this, R.style.AlertDialogCustom));
            builder.setTitle("Please Select");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0)
                        startActivity(new Intent(Main_Room.this, WeatherRoom.class));
                    else if (which ==1)
                        startActivity(new Intent(Main_Room.this, DataScreen.class));
                    else if (which ==2){
                        database.saveDB();
                        Toast.makeText(getApplicationContext(), "DB Saved", Toast.LENGTH_LONG).show();
                    }
                    else if (which ==3){
                        database.updateShift();
                        Toast.makeText(getApplicationContext(), "Shift has been updated to " + database.getShiftNumber(), Toast.LENGTH_LONG).show();
                        Intent i = new Intent();
                        i.setClass(getApplicationContext(), Main_Room.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().getApplicationContext().startActivity((i));
                        finish();
                        database.closeDatabase();
                    }
                }
            });
            builder.show();
        }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private View.OnClickListener tapScreen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    };

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
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@SuppressWarnings("NullableProblems") MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_login)
            selectItem();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectItem() {
        switch(1) {
            case 1:
                loginID();
                break;
            default:
        }
    }

    private void dataLogin() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Admin Login");
        alert.setMessage("Please Enter Password");
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
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Returning to Menu", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    private void loginID(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Login with ID");
        alert.setMessage("Please use your 6 digit code");
        alert.setCancelable(false);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6)});
        alert.setView(input);
        setViewable();
        viewController.viewInput();
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    if(input.getText().toString().length() ==6) {
                        idNow = input.getText().toString();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Must be 6 digits", Toast.LENGTH_SHORT).show();
                        loginID();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "\t\t\tSorry invalid input\nonly 6 digits are acceptable", Toast.LENGTH_LONG).show();
                    loginID();
                }
                if (!notUsedNotification)
                    unusedNotification();
                else{
                    notifyUsers.cancel();
                    Log.d("Main_Room","unusedNotification cancelled");
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "Back to the menu ", Toast.LENGTH_SHORT).show();
                cancelledInput();
            }
        });
        alert.show();
    }

    private void checkWeather(){
        Double x = database.getRoomMedian();
        Log.d("Main_Room","Recalculation of the median is: "+ x);
        if (x != 0.0) {
            if(x == 1.0)
                viewController.showThunder();
            else if(x ==2.0 || x == 1.5)
                viewController.showRainMood();
            else if(x ==3.0|| x == 2.5)
                viewController.showOvercast();
            else if(x ==4.0|| x == 3.5)
                viewController.showClouds();
            else if(x==5.0|| x == 4.5)
                viewController.showSun();
        } else
            viewController.startUp();
    }

    private void showNurses(){
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
            else maxedNurses();
        }

    private void maxedNurses(){
        if(sub){
            NurseTimer oldNurse = getTimer(counter.getCount());
            oldNurse.maxedReached();
            database.changedMind(oldNurse.getNurseId());
            final ImageView newNurse = nurseArray.get(counter.getCount());
            newNurse.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewController.fadeTheImage(newNurse,1,0,View.VISIBLE);
                }
            },1000);
            setTimer(newNurse,counter.getCount());
            getTimer(counter.getCount()).startTimer();
            counter.setCount();
            if (counter.getCount() == nurseArray.size()) {
                counter.resetCount();
                Log.d("Main_Room","NurseArray has been reset");
            }
        }
    }

    private void feelingChanged(){
        if(counter.getCount() == 0 && !sub) {
            Log.d("Main_Room","This ID is already in the Database but the room is empty: "+idNow);
            ImageView nurseView = nurseArray.get(counter.getCount());
            nurseView.setVisibility(View.VISIBLE);
            setTimer(nurseView,counter.getCount());
            getTimer(counter.getCount()).startTimer();
            counter.setCount();
        }
        else if(counter.getCount() > 0){
            final int newCount = nurseMap.get(idNow);
            final ImageView nurseView = nurseArray.get(newCount);
            getTimer(newCount).maxedReached();
            database.changedMind(getTimer(newCount).getNurseId());
            Log.d("Main_Room","feelingChanged new Nurse: "+ idNow);
            if (!sub) { //boolean check to see if mx number of nurses already visible
                        viewController.fadeTheImage(nurseView,1,0,View.VISIBLE);
                        setTimer(nurseView,newCount);
                        getTimer(newCount).startTimer();
//                        counter.setCount();
                if (counter.getCount() == nurseArray.size()) {
                    counter.resetCount();
                    sub = true;
                }
            }
            else maxedNurses();
        }
    }

    private void unusedNotification(){
        notUsedNotification = true;
        Log.d("Main_Room","unusedNotification has started: "+ true);
        notifyUsers =new CountDownTimer((1000 * 60 * 20), (1000 * 60 * 20)) {
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
            database.dbClearScreen();
        else
            Log.d("Main_Room","clearScreen() "+"There are no outlying inputs");
    }

    private void setTimer(View v,int countDownID) {
        Log.d("Main_Room","setTimer is starting: " + countDownID);
        final ImageView iv = (ImageView) v;
        final String nurseId = idNow;
        nurseMap.put(nurseId,countDownID);
        if(countDownID ==0)
            nurseTimer1.setInfo(iv,nurseId);
        else if (countDownID ==1)
            nurseTimer2.setInfo(iv,nurseId);
        else if (countDownID ==2)
            nurseTimer3.setInfo(iv,nurseId);
        else if (countDownID ==3)
            nurseTimer4.setInfo(iv,nurseId);
        else if (countDownID ==4)
            nurseTimer5.setInfo(iv,nurseId);
        else if (countDownID ==5)
            nurseTimer6.setInfo(iv,nurseId);
        else if (countDownID ==6)
            nurseTimer7.setInfo(iv,nurseId);
    }

    private void setNurseTimers(){
        nurseTimer1 = new NurseTimer(database);
        nurseTimer2 = new NurseTimer(database);
        nurseTimer3 = new NurseTimer(database);
        nurseTimer4 = new NurseTimer(database);
        nurseTimer5 = new NurseTimer(database);
        nurseTimer6 = new NurseTimer(database);
        nurseTimer7 = new NurseTimer(database);
    }

    private NurseTimer getTimer(int timerID){
        Log.d("Main_Room","timerID: " + timerID+ " has been called.");
            if(timerID ==0)
                return nurseTimer1;
            else if(timerID ==1)
                return nurseTimer2;
            else if(timerID ==2)
                return nurseTimer3;
            else if(timerID ==3)
                return nurseTimer4;
            else if(timerID ==4)
                return nurseTimer5;
            else if(timerID ==5)
                return nurseTimer6;
            else if(timerID == 6)
                return nurseTimer7;
            else {
                Log.d("Main_Room","NULL");
                return null;
            }
    }

    private View.OnClickListener stormyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 1.0;
            inputOverlay.setImageResource(R.drawable.input_thunderstorm);
            setFinishedInput();
        }
    };
    private View.OnClickListener rainyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 2.0;
            inputOverlay.setImageResource(R.drawable.input2_rainy);
            setFinishedInput();
        }
    };
    private View.OnClickListener overcastClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 3.0;
            inputOverlay.setImageResource(R.drawable.input3_clouded);
            setFinishedInput();
        }
    };
    private View.OnClickListener cloudyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 4.0;
            inputOverlay.setImageResource(R.drawable.input4_half_clouded);
            setFinishedInput();
        }
    };
    private View.OnClickListener sunnyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 5.0;
            inputOverlay.setImageResource(R.drawable.input5_sunny);
            setFinishedInput();
        }
    };

    private void setViewable() {
        stormy.setVisibility(View.VISIBLE);
        rainy.setVisibility(View.VISIBLE);
        overcast.setVisibility(View.VISIBLE);
        cloudy.setVisibility(View.VISIBLE);
        sunny.setVisibility(View.VISIBLE);
        stormy.setClickable(true);
        rainy.setClickable(true);
        overcast.setClickable(true);
        cloudy.setClickable(true);
        sunny.setClickable(true);
    }

    private void setInvisible() {
        stormy.setVisibility(View.GONE);
        rainy.setVisibility(View.GONE);
        overcast.setVisibility(View.GONE);
        cloudy.setVisibility(View.GONE);
        sunny.setVisibility(View.GONE);
        stormy.setClickable(false);
        rainy.setClickable(false);
        overcast.setClickable(false);
        cloudy.setClickable(false);
        sunny.setClickable(false);
    }

    private void cancelledInput(){
        setInvisible();
        viewController.viewNurses();
        inputOverlay.setVisibility(View.GONE);
        checkWeather();
        viewController.setBack();
    }

    private void setFinishedInput() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Double avg = database.getAverage(mood);
        final String query = "INSERT into nurses(`id`,`input`,`median`,`date`,`shift_id`,`inputDate`,`changed`)" +
                "VALUES('" + idNow + "','"+ mood +"','"+ avg +"','"+ currentDateTimeString +"','"+ database.getShiftNumber()+"','"+
                database.getDay()+"','"+ 0 +"');";
        database.addMedian(avg,currentDateTimeString,database.getShiftNumber());
        final int factCheck = database.factCheck(idNow);
        if(factCheck == 1) {
            Log.d("Main_Room","factCheck is 1");
            database.changedMind(idNow);
            database.execSQL(query);
        }
        else if(factCheck == 0) {
            Log.d("Main_Room","factCheck is 0");
            database.execSQL(query);
        }
        setInvisible();
        viewController.afterInput();
        checkWeather();
        boolean found = nurseMap.containsKey(idNow);
                if(found) {
                    Log.d("Main_Room","already here " + true);
                    feelingChanged();
                    }
                    else{
                    Log.d("Main_Room","not here here " + false);
                    showNurses();
                }
    }

}