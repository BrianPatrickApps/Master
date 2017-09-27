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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Main_Room extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,Serializable{
    /**
     * @param db the Database object controlling all the SQLite database.
     * @param inputOverlay holds image of the input for users to pick.
     * @param rainOverlay holds image of the gif to show rain.
     * @param weatherOverlay holds images of the weather corresponding to the median of the room.
     * @param control object that controls the inputs of users to be inserted into the SQLite Database.
     * @param viewController object that controls the images on screen corresponding
     *                       to data from SQLite Database given by users.
     * @param sub Boolean that checks in the max amount of nurse images has been used.
     * @param notUsedNotification Boolean that checks if the hasn't been used for 20 mins notification is called
     * @param notifyUsers CountDownTimer that is started when I user inputs, if the app is not used within 20 mins
     *                    notification is called has for input.It is cancelled if there is input before the timer
     *                    runs out.
     * @param nurseTimer1 Object holding the ID of the user, Nurse Image representing them and the timer that starts
     *                    when they input which after 2 hours will erase them from the screen, remove them from the sample
     *                    the median is being calculated making their input null but saving their data in the database.
     * @param nurseArray  ArrayList holding the 7 nurse images for users to use when they input.
     * @param counter Object that keeps count of the number of users that are being displayed.
     *
     */
    private Database db;
    private ButtonController control;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        counter = new Counter();
        db = new Database(this);
        Log.d("Main_Room","App has started, the shift number is " + db.getShiftNumber());

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
        ImageView inputOverlay = (ImageView) findViewById(R.id.inputWeather);

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.relLay);
        rel.setOnClickListener(tapScreen);
        Button stormy = (Button) findViewById(R.id.Stormy);
        Button rainy = (Button) findViewById(R.id.Rain);
        Button overcast = (Button) findViewById(R.id.Overcast);
        Button cloudy = (Button) findViewById(R.id.Cloudy);
        Button sunny = (Button) findViewById(R.id.Sunny);




        viewController = new ViewController(rel,rel2,rel3, rainOverlay, weatherOverlay, inputOverlay);
        viewController.startUp();

        control = new ButtonController(stormy, rainy, overcast, cloudy, sunny, inputOverlay,getApplicationContext(),viewController);
        control.setInvisible();
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
        db.updateDate(formattedDate);

        View header = navigationView.getHeaderView(0);
        ImageView nurseButton = (ImageView)header.findViewById(R.id.TUNURSE);
        nurseButton.setOnClickListener(new View.OnClickListener() {
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
     * nurseMenu() Is the method called when an administrator wants to use the administrator controls that can only
     * be accessed by a verification of a password that only the admin knows.
     * Within this method admin can check the all the inputs of the users within that shift.
     * Check the current variables of the shift.
     * Force save the database into a csv file
     * Force change the shift
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

    /**
     * onBackPressed()
     * When the menu and the back button is pressed then it closes the menu.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * tapScreen
     * A button listener for a button that encompasses the whole screen in which when pressed
     * opens the menu for the user but also closes it when the menu is open.
     */
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
     * @param item selected Input from menu which can only be loginID
     * @return true
     */
    public boolean onNavigationItemSelected(@SuppressWarnings("NullableProblems") MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            selectItem();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Menu controller that opens the needed method for the user, since there is only one
     * option,so 1 is given to open the menu item for users to input.
     */
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

    /**
     * dataLogin()
     * This method collects the password from the admin which allows them to access the nurseMenu.
     */
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
        alert.show();
    }

    /**
     * loginID method that collects the 6 digit ID of the user and sends it to the ButtonController,
     * which INSERTS the data of the user into the SQLite Database.
     * After inserting a nurse ImageView appears that corresponds to the user, the median is recalculated
     * based on the number of nurses in the room or in the database
     * After all the SQL operations other methods are called showNurses()
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
                control.cancelledInput();
            }
        });
        alert.show();
    }

    /**
     * checkWeather calculates or calculates the median based on the number of nurses that are in the room
     * @param db SQLite controller which calls the method getRoomMedian which collects the median of the
     *           room based on the sample of nurses that are being displayed in the room. If the room is
     *           empty then it calls another method which clears the screen only for when the app is starting
     *           or a reset has occurred.
     * @param viewController Object that controls all the images that correlates to the median. It shows
     *                       the overall background, the light of weather, the weather itself and the rain.
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


    private void showNurses(){
        int reDo =db.doOver(idNow);
        if(reDo == 1)
            changeMind();
        else if(reDo == 0) {
            Log.d("Main_Room","new Nurse is being displayed");
            ImageView nurseView = nurseArray.get(counter.getCount());
            //starts setting nurses invisible manually if the max is visible.
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
    }

    private void maxedNurses(){
        if(sub){
            getTimer(counter.getCount()).maxedReached();
            getTimer(counter.getCount()).closeNurseImage();
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
            if (counter.getCount() == 7) {
                counter.resetCount();
                Log.d("Main_Room","NurseArray has been reset");
            }
        }
    }

    private void changeMind(){
        if(counter.getCount() == 0 && !sub) {
            Log.d("Main_Room","This ID is already in the Database but the room is empty: "+idNow);
            ImageView nurseView = nurseArray.get(counter.getCount());
            nurseView.setVisibility(View.VISIBLE);
            counter.setCount();
        }
        else if(counter.getCount() > 0){
            counter.removeCount();
            final ImageView nurseView = nurseArray.get(counter.getCount());
            nurseView.setVisibility(View.GONE);
            Log.d("Main_Room","This ID is already in the Database no timer will be called: "+ idNow);
            final ImageView nurseImage = nurseArray.get(counter.getCount());
            //starts setting nurses invisible manually if the max is visible.
            if (!sub) { //boolean check to see if mx number of nurses already visible
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewController.fadeTheImage(nurseView,1,0,View.VISIBLE);
                    }
                },1000);
                counter.setCount();
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
            db.dbClearScreen();
        else
            Log.d("Main_Room","clearScreen() "+"There are no outlying inputs");
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