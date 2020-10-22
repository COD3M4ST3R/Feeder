package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    // XML VARIABLE INITIALIZE
    Button button_Start_Register, button_Start_Login;

    // FIREBASE USER CHECK
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // CASTING XML VARIABLES
        button_Start_Register = findViewById(R.id.button_Start_Register);
        button_Start_Login = findViewById(R.id.button_Start_Login);

        auth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            int flag = bundle.getInt("Flag");
            auth.signOut();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    open_MainActivity();
                }
            }
        };


        button_Start_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_RegisterActivity();
            }
        });

        button_Start_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_LoginActivity();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);
    }


    // INTENT OF MAIN ACTIVITY
    public void open_MainActivity(){
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // INTENT OF LOGIN ACTIVITY
    public void open_LoginActivity(){
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // INTENT OF REGISTER ACTIVITY
    public void open_RegisterActivity(){
        Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
