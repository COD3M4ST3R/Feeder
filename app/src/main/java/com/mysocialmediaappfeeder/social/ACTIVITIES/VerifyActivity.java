package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyActivity extends AppCompatActivity
{
    //  XML
    EditText email;
    Button verify;

    String e;

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
        setContentView(R.layout.activity_verify);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //  XML
        email = findViewById(R.id.email);
        verify = findViewById(R.id.verify);


        verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                e = email.getText().toString();

                if(e.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                {
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(VerifyActivity.this, "Verification has been sent to your E-Mail", Toast.LENGTH_SHORT).show();

                            Toast.makeText(VerifyActivity.this, "Verification has been sent to your E-Mail", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(VerifyActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(VerifyActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else
                {
                    Toast.makeText(VerifyActivity.this, "Please enter your E-Mail", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
