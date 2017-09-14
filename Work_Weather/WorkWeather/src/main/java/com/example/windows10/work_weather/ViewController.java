package com.example.windows10.work_weather;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.Serializable;


class ViewController implements Serializable{

    private RelativeLayout mainScreen;
    @SuppressWarnings("deprecation")
    private AbsoluteLayout nurse;
    private RelativeLayout inputScreen;
    private ImageView rainOverlay;
    private ImageView weatherOverlay;
    private ImageView inputOverlay;

    ViewController(RelativeLayout mainScreen, @SuppressWarnings("deprecation") AbsoluteLayout nurse, RelativeLayout inputScreen, ImageView rainOverlay, ImageView weatherOverlay,ImageView inputOverlay){
        this.mainScreen = mainScreen;
        this.nurse = nurse;
        this.inputScreen = inputScreen;
        this.rainOverlay = rainOverlay;
        this.weatherOverlay = weatherOverlay;
        this.inputOverlay = inputOverlay;
    }

    void viewNurses() {
        nurse.setVisibility(View.VISIBLE);
        inputScreen.setVisibility(View.GONE);
    }

    void viewInput(){
        inputScreen.setVisibility(View.VISIBLE);
    }

    private void setRain(){

        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                rainOverlay.setVisibility(View.VISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        rainOverlay.startAnimation(fadeOut);
    }

    void stopRain(){
        rainOverlay.setVisibility(View.GONE);
    }

    void showSun(){
        fadeOutAndHideImage();
        weatherOverlay.setImageResource(R.drawable.weather_sun);
        mainScreen.setBackgroundResource(R.drawable.background5_sunny);
    }

    void showClouds(){
        fadeOutAndHideImage();
        weatherOverlay.setImageResource(R.drawable.weather_halfclouds);
        mainScreen.setBackgroundResource(R.drawable.background4_semi_clouded);
    }

    void showOvercast(){
        fadeOutAndHideImage();
        weatherOverlay.setImageResource(R.drawable.weather_clouds);
        mainScreen.setBackgroundResource(R.drawable.background3_clouded);
    }

    void showRainMood(){
        fadeOutAndHideImage();
        weatherOverlay.setImageResource(R.drawable.weather_rain);
        setRain();
        mainScreen.setBackgroundResource(R.drawable.background2_rain);
    }

    void showThunder(){
        fadeOutAndHideImage();
        weatherOverlay.setImageResource(R.drawable.weather_thunder);
        setRain();
        mainScreen.setBackgroundResource(R.drawable.background1_thunderstorm);
    }

    void startUp(){
        viewNurses();
        weatherOverlay.setImageResource(R.drawable.weather_start);
        mainScreen.setBackgroundResource(R.drawable.background3_clouded);
        stopRain();
    }

    void setBack(){
        inputOverlay.setImageResource(R.drawable.input_1);
    }

    private void fadeOutAndHideImage()
    {
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                weatherOverlay.setVisibility(View.VISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        weatherOverlay.startAnimation(fadeOut);
    }

    void fadeOut()
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

            fadeOut.setAnimationListener(new Animation.AnimationListener()
            {
                public void onAnimationEnd(Animation animation)
                {
                    weatherOverlay.setVisibility(View.GONE);
                }
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationStart(Animation animation) {}
            });

        weatherOverlay.startAnimation(fadeOut);
    }

}
