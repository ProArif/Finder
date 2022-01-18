package com.nodeers.finder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=3000;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //After completion of 2000 ms, the next activity will get started.
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //This method is used so that  splash activity
            //can cover the entire screen.

        toolbar = getSupportActionBar();
        toolbar.hide();

            setContentView(R.layout.activity_splash);
            //this will bind your MainActivity.class file with activity_main.

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(Splash.this,
                            MainActivity.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.

                    finish();
                    //the current activity will get finished.
                }
            }, SPLASH_SCREEN_TIME_OUT);
        }
    }
