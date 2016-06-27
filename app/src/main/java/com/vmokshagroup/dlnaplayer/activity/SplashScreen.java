package com.vmokshagroup.dlnaplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.vmokshagroup.dlnaplayer.R;
import com.vmokshagroup.dlnaplayer.modelclass.ModelClass;

public class SplashScreen extends AppCompatActivity {


    private static int SPLASH_SCREEN_TIME_OUT = 3000;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                sharedPreferences = getApplicationContext().getSharedPreferences(ModelClass.MediaServerPreference, Context.MODE_PRIVATE);
                //if (sharedPreferences.contains(ModelClass.isLogin)) {
                if (sharedPreferences.getInt(ModelClass.isLogin, 0) == 1) {
                    SplashScreen.this.finish();
                    startActivity(new Intent(SplashScreen.this, MediaServerActivity.class));
                    /*overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);*/
                } else {
                    SplashScreen.this.finish();
                    startActivity(new Intent(SplashScreen.this, MediaServerActivity.class));
                    /*overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);*/
                }
            }
            // }
        }, SPLASH_SCREEN_TIME_OUT);

    }
}
