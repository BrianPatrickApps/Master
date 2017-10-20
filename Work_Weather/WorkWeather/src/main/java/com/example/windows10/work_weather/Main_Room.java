package com.example.windows10.work_weather;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Main_Room extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Database database;
    private ViewController viewController;

    private ArrayList<ImageView> nurseArray;
    private Counter counter;

    private Button stormy;
    private Button rainy;
    private Button overcast;
    private Button cloudy;
    private Button sunny;
    private ImageView inputOverlay;
    private Double mood;
    private Boolean fullRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        counter = new Counter();
        database = new Database(this);
        Log.d("Main_Room","App has started, the shift number is " + database.getShiftNumber());

        RelativeLayout inputScreen = (RelativeLayout)findViewById(R.id.inputScreen);
        AbsoluteLayout nurseScreen = (AbsoluteLayout) findViewById(R.id.Nurse);
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


        RelativeLayout touchScreen = (RelativeLayout)findViewById(R.id.relLay);
        touchScreen.setOnClickListener(tapScreen);
        
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
        
        viewController = new ViewController(touchScreen,nurseScreen,inputScreen, rainOverlay, weatherOverlay, inputOverlay);
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
        Calendar c = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        database.updateDate(formattedDate);

        View header = navigationView.getHeaderView(0);
        ImageView nurseButton = (ImageView)header.findViewById(R.id.TUNURSE);
        nurseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dataLogin();
            }
        });
        clearScreen();
        fullRoom = false;
    }

    private void nurseMenu(){
            final String[] option = {"Data Table","Current Data","Save","Change"};
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
//                        database.saveDB();
                        Toast.makeText(getApplicationContext(), "DB Saved", Toast.LENGTH_LONG).show();
                    }
                    else if (which ==3){
                        database.updateShift();
                        Toast.makeText(getApplicationContext(), "Shift has been updated to " +
                                database.getShiftNumber(), Toast.LENGTH_LONG).show();
                        Intent restartIntent = new Intent();
                        restartIntent.setClass(getApplicationContext(), Main_Room.class);
                        restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity((restartIntent));
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
            setViewable();
            viewController.viewInput();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main__room, menu);
        return true;
    }

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
//                loginID();
                setViewable();
                viewController.viewInput();
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
                    if (id == 0)
                        nurseMenu();
                    else
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

    private void checkWeather(){
        Double roomMean = database.getRoomMedian();
        Log.d("Main_Room","Recalculation of the mean is: "+ roomMean);
        if (roomMean != 0.0) {
            if(roomMean >= 0.6 && roomMean < 1.6)
                viewController.showThunder();
            else if(roomMean >= 1.6 && roomMean < 2.6)
                viewController.showRainMood();
            else if(roomMean >= 2.6 && roomMean < 3.6)
                viewController.showOvercast();
            else if(roomMean >= 3.6 && roomMean < 4.6)
                viewController.showClouds();
            else if(roomMean >= 4.6 && roomMean < 5.6)
                viewController.showSun();
        } else
            viewController.startUp();
    }

    //Change at x.6 to x+1.6
    //new 1 background
    //Calculate mean
    //new how do you feel
    //no id just simple version
    //8th person reset new db table that holds them or turn everything off, turn off timers no need for alarms
    //no need to save database

    //Hospital
    //mean
    //new background
    //round
    //if someone puts 1 or 2 new nurse image
    // new nurses

    private void showNurses(){
            Log.d("Main_Room","new Nurse is being displayed");
            ImageView nurseView = nurseArray.get(counter.getCount());
            if(mood == 1 || mood ==2) {
                if(counter.getCount() ==0)
                    nurseView.setImageResource(R.drawable.nurse_1b);
                else if(counter.getCount() ==1)
                    nurseView.setImageResource(R.drawable.nurse_2b);
                else if(counter.getCount() ==2)
                    nurseView.setImageResource(R.drawable.nurse_3b);
                else if(counter.getCount() ==3)
                    nurseView.setImageResource(R.drawable.nurse_4b);
                else if(counter.getCount() ==4)
                    nurseView.setImageResource(R.drawable.nurse_5b);
                else if(counter.getCount() ==5)
                    nurseView.setImageResource(R.drawable.nurse_6b);
                else
                    nurseView.setImageResource(R.drawable.nurse_7b);
            }
            if (!fullRoom) { //boolean check to see if mx number of nurses already visible
                nurseView.setVisibility(View.VISIBLE);
                counter.setCount();
                checkWeather();
                if (counter.getCount() == nurseArray.size()) {
                    fullRoom = true;
                    counter.resetCount();
                }
            }
            else{
                for (ImageView aNurseArray : nurseArray)
                    aNurseArray.setVisibility(View.GONE);
                fullRoom = false;
                nurseArray.get(counter.getCount()).setVisibility(View.VISIBLE);
                database.dbClearScreen();
                setFinishedInput();
            }
        }

    private void clearScreen(){
        if(counter.getCount() ==0)
            database.dbClearScreen();
        else
            Log.d("Main_Room","clearScreen() "+"There are no outlying inputs");
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

    //Calculate the mean

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

    private void setFinishedInput() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Double currentAverage = database.getAverage(mood);
        final String query = "INSERT into nurses(`input`,`median`,`date`,`shift_id`,`inputDate`)" +
                "VALUES('" + mood +"','"+ currentAverage +"','"+ currentDateTimeString +"','"+ database.getShiftNumber()+"','"+
                database.getDay()+"');";
        database.execSQL(query);
        setInvisible();
        viewController.afterInput();
        checkWeather();
        showNurses();
    }

}