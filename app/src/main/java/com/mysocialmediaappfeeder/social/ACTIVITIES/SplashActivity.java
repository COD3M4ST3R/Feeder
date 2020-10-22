package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.SharedPrefs;

public class SplashActivity extends AppCompatActivity
{
    SharedPrefs sharedPrefs;

    //  XML
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        sharedPrefs = new SharedPrefs(this);

        if(sharedPrefs.loadNightMode() == true)
        {
            setTheme(R.style.DarkMode);
        }

        else
        {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //  XML
        imageView = findViewById(R.id.imageView);

        Glide.with(getApplicationContext()).load(R.drawable.logo).into(imageView);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // SPLASH SCREEN
        Thread timerThread = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(500);

                }catch(InterruptedException e)
                {
                    e.printStackTrace();

                }finally
                {
                    Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                    startActivity(intent);
                }

            }
        };
        timerThread.start();
    }
}
