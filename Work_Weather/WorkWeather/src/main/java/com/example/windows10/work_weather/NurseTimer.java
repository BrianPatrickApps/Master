package com.example.windows10.work_weather;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class NurseTimer {
    private ImageView nurseImage;
    private ImageView rainOverlay;
    private String nurseId;
    private CountDownTimer nurseTimer;


    NurseTimer(final Database database, final ImageView weatherOverlay,final RelativeLayout mainScreen,final ImageView rainOverlay){

        this.rainOverlay = rainOverlay;
        nurseTimer = new CountDownTimer(7200000, 10000) {
//                    nurseTimer = new CountDownTimer((1000 * 30 ), (10000)) {
            public void onTick(long millisUntilFinished) {
                Log.d("Main_Room", nurseId +" is Working");
            }
            public void onFinish() {
                nurseImage.setVisibility(View.GONE);
                Log.d("NurseTimer","nurseTimeout() "+"Removed");
                database.changedMind(nurseId);
                Double roomMean = database.getRoomAverage();
                Log.d("Main_Room","Recalculation of the mean is: "+ roomMean);
                if (roomMean != 0.0) {
                    if(roomMean >= 0.6 && roomMean < 1.6)
                        showThunder(weatherOverlay,mainScreen);
                    else if(roomMean >= 1.6 && roomMean < 2.6)
                        showRainMood(weatherOverlay,mainScreen);
                    else if(roomMean >= 2.6 && roomMean < 3.6)
                        showOvercast(weatherOverlay,mainScreen);
                    else if(roomMean >= 3.6 && roomMean < 4.6)
                        showClouds(weatherOverlay,mainScreen);
                    else if(roomMean >= 4.6 && roomMean < 5.6)
                        showSun(weatherOverlay,mainScreen);
                } else
                    startUp(weatherOverlay,mainScreen);
            }
        };
    }

    void setInfo(final ImageView nurseImage, final String nurseId){
        this.nurseImage = nurseImage;
        this.nurseId = nurseId;
    }

    void startTimer(){
        nurseTimer.start();
        Log.d("Main_Room","NurseTimer has started " + nurseId);
    }

    String returnID(){
        return nurseId;
    }

    void maxedReached(){
        nurseTimer.cancel();
        nurseImage.setVisibility(View.GONE);
        Log.d("NurseTimer","nurse is closed and timer is cleared: " + nurseId);
    }

    private void setRain(){
        fadeTheImage(rainOverlay, View.VISIBLE);
    }

    private void stopRain(){
        rainOverlay.setVisibility(View.GONE);
    }

    private void showSun(final ImageView weatherOverlay, final RelativeLayout mainScreen){
        fadeTheImage(weatherOverlay, View.VISIBLE);
        weatherOverlay.setImageResource(R.drawable.weather_sun);
        mainScreen.setBackgroundResource(R.drawable.background5_sunny);
        stopRain();
    }

    private void showClouds(final ImageView weatherOverlay, final RelativeLayout mainScreen){
        fadeTheImage(weatherOverlay, View.VISIBLE);
        weatherOverlay.setImageResource(R.drawable.weather_halfclouds);
        mainScreen.setBackgroundResource(R.drawable.background4_semi_clouded);
        stopRain();
    }

    private void showOvercast(final ImageView weatherOverlay, final RelativeLayout mainScreen){
        fadeTheImage(weatherOverlay, View.VISIBLE);
        weatherOverlay.setImageResource(R.drawable.weather_clouds);
        mainScreen.setBackgroundResource(R.drawable.background3_clouded);
        stopRain();
    }

    private void showRainMood(final ImageView weatherOverlay, final RelativeLayout mainScreen){
        fadeTheImage(weatherOverlay, View.VISIBLE);
        weatherOverlay.setImageResource(R.drawable.weather_rain);
        mainScreen.setBackgroundResource(R.drawable.background2_rain);
        setRain();
    }

    private void showThunder(final ImageView weatherOverlay, final RelativeLayout mainScreen){
        fadeTheImage(weatherOverlay, View.VISIBLE);
        weatherOverlay.setImageResource(R.drawable.weather_thunder);
        setRain();
        mainScreen.setBackgroundResource(R.drawable.background1_thunderstorm);
    }

    private void startUp(final ImageView weatherOverlay, final RelativeLayout mainScreen){
        weatherOverlay.setImageResource(R.drawable.weather_start);
        mainScreen.setBackgroundResource(R.drawable.background3_clouded);
        stopRain();
    }

    private void fadeTheImage(final ImageView img, final int visibility){
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation) {img.setVisibility(visibility);}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        img.startAnimation(fadeOut);
    }

}
