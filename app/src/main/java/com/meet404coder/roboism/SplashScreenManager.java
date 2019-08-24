package com.meet404coder.roboism;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class SplashScreenManager extends AppCompatActivity {

    Animation animation;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);
        ImageView spl = (ImageView) findViewById(R.id.splashscreenimageview);

        Picasso.with(SplashScreenManager.this).load(R.drawable.blk_logo_splash).fit().into(spl);

        animation = AnimationUtils.loadAnimation(SplashScreenManager.this, R.anim.fade_in);
        spl.setAnimation(animation);

        prefManager = new PrefManager(SplashScreenManager.this);

        if (!prefManager.isFirstTimeLaunch()) {  //if this app is not launched for the first time
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intentHome = new Intent(SplashScreenManager.this, Dashboard.class);
                    finish();
                    startActivity(intentHome);

                }
            }, 2800);

        } else {        //If the app is run for the first time on a device
            //TODO: Add Intro

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intentHome = new Intent(SplashScreenManager.this, GuestOrLogin.class);
                    finish();
                    startActivity(intentHome);
                }
            }, 2800);
        }
    }


}
