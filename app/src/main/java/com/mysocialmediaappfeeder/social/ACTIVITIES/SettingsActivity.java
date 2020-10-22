package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.SharedPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity
{
    //  XML VARIABLES
    Switch switchColorMode;
    Button deleteAccount;
    Button verify;
    ImageView verified;
    Button security;
    Switch switch_private;

    //  SHAREDPREFS
    SharedPrefs sharedPrefs;

    //  USER
    User user;


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
        setContentView(R.layout.activity_settings);

        //  XML VARIABLES
        switchColorMode = findViewById(R.id.switchColorMode);
        deleteAccount = findViewById(R.id.deleteAccount);
        verify = findViewById(R.id.verify);
        verified = findViewById(R.id.verified);
        security = findViewById(R.id.security);
        switch_private = findViewById(R.id.switch_private);


        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(sharedPrefs.loadNightMode() == true)
        {
            switchColorMode.setChecked(true);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //  CHECK IF ACCOUNT VERIFIED OR NOT
        if(firebaseUser.isEmailVerified())
        {
            verified.setVisibility(View.VISIBLE);
            verify.setVisibility(View.INVISIBLE);
        }

        else
        {
            verified.setVisibility(View.INVISIBLE);
            verify.setVisibility(View.VISIBLE);
        }

        //  DARK MODE LISTENER
        switchColorMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    sharedPrefs.setNightMode(true);
                    restartApp();
                }

                else
                {
                    sharedPrefs.setNightMode(false);
                    restartApp();
                }
            }
        });


        //  VERIFIED TOAST MESSAGE DISPLAY
        verified.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(SettingsActivity.this, "Account is Verified!", Toast.LENGTH_SHORT).show();
            }
        });

        //  DELETE ACCOUNT LISTENER
        deleteAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //  OPEN "DeleteAccountActivity"
                open_DeleteAccountActivity();
            }
        });


        //  VERIFY LISTENER
        verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, VerifyActivity.class);
                startActivity(intent);
            }
        });


        //  SECURITY LISTENER
        security.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


        //  PRIVATE ACCOUNT SWITCH LISTENER
        SharedPreferences sharedPreferences = getSharedPreferences("accountType", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        SharedPreferences sharedPreferences1 = getSharedPreferences("accountType", MODE_PRIVATE);
        switch_private.setChecked(sharedPreferences1.getBoolean("accountType", false));


        switch_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(switch_private.isChecked())
                {
                    //  Make Account Private.
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                    builder.setCancelable(true);
                    builder.setTitle("Make Account Private?");
                    builder.setMessage("Only users that you are following can see your profile in private mode.");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                            switch_private.setChecked(false);

                            editor.putBoolean("accountType", false);
                            editor.apply();
                        }
                    });

                    builder.setPositiveButton("Make it Private", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch_private.setChecked(true);

                            editor.putBoolean("accountType", true);
                            editor.apply();

                            String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            FirebaseDatabase.getInstance().getReference("Users").child(currentID).child("accountType").setValue("private");

                        }
                    });
                    builder.show();
                }

                else
                {
                    //  Make Account Public.
                    editor.putBoolean("accountType", false);
                    editor.apply();

                    String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseDatabase.getInstance().getReference("Users").child(currentID).child("accountType").setValue("public");
                }
            }
        });

    }


    //  METHODs
    private void restartApp()
    {
        System.exit(0);
    }

    private void open_DeleteAccountActivity()
    {
        Intent intent = new Intent(SettingsActivity.this, DeleteAccountActivity.class);
        startActivity(intent);
    }
}
