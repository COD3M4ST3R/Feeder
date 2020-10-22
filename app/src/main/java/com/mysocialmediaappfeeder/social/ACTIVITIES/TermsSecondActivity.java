package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.SharedPrefs;

public class TermsSecondActivity extends AppCompatActivity
{

    SharedPrefs sharedPrefs;

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
        setContentView(R.layout.activity_terms_second);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
