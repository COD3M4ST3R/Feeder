package com.mysocialmediaappfeeder.social;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs
{
    SharedPreferences sharedPreferences;

    public SharedPrefs(Context context)
    {
        sharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setNightMode(Boolean state)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    public Boolean loadNightMode()
    {
        Boolean state = sharedPreferences.getBoolean("NightMode", false);

        return state;
    }
}
