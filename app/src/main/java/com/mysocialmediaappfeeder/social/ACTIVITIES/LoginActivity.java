package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mysocialmediaappfeeder.social.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // INITIALIZE XML VARIABLES
    EditText editText_Login_Email, editText_Login_Password;
    Button Button_Login;
    Button button_Login_RegisterHere;
    Button resetPassword;

    // FIREBASE AUTH
    FirebaseAuth auth;

    // PROGRES BAR
    ProgressBar indeterminateBar_Login;

    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // CASTING XML VARIABLE
        editText_Login_Email = findViewById(R.id.editText_Login_Email);
        editText_Login_Password = findViewById(R.id.editText_Login_Password);
        Button_Login = findViewById(R.id.Button_Login);
        button_Login_RegisterHere = findViewById(R.id.button_Login_RegisterHere);
        indeterminateBar_Login = findViewById(R.id.indeterminateBar_Login);
        resetPassword = findViewById(R.id.resetPassword);


        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // MAKE PROGRESS BAR INVISIBLE
        indeterminateBar_Login.setVisibility(View.INVISIBLE);

        // MAKE LOGIN BUTTON VISIBLE
        Button_Login.setVisibility(View.VISIBLE);

        // FIREBASE INSTANCE
        auth = FirebaseAuth.getInstance();

        // REGISTER BUTTON IN LOGIN
        button_Login_RegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_RegisterActivity();
            }
        });


        //  RESET PASSWORD CLICK LISTENER
        resetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_ResetPasswordActivity();
            }
        });

        // LOGIN BUTTON
        Button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Login_Email = editText_Login_Email.getText().toString();
                final String Login_Password = editText_Login_Password.getText().toString();

                // MAKE LOGIN BUTTON INVISIBLE
                Button_Login.setVisibility(View.INVISIBLE);

                // MAKE PROGRESS BAR VISIBLE
                indeterminateBar_Login.setVisibility(View.VISIBLE);

                if(TextUtils.isEmpty(Login_Email) || TextUtils.isEmpty(Login_Password)){
                    Toast.makeText(LoginActivity.this, "Fields Cannot Be Empty", Toast.LENGTH_SHORT).show();

                    // MAKE LOGIN BUTTON VISIBLE
                    Button_Login.setVisibility(View.VISIBLE);

                    // MAKE PROGRESS BAR INVISIBLE
                    indeterminateBar_Login.setVisibility(View.INVISIBLE);

                }else{
                    auth.signInWithEmailAndPassword(Login_Email, Login_Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        open_MainActivity();

                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {

                                    }
                                });

                                // MAKE PROGRESS BAR GONE
                                indeterminateBar_Login.setVisibility(View.GONE);

                            } else{
                                Toast.makeText(LoginActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();

                                // MAKE PROGRESS BAR INVISIBLE
                                indeterminateBar_Login.setVisibility(View.INVISIBLE);
                                Button_Login.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    public void open_RegisterActivity()
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void open_MainActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void open_ResetPasswordActivity()
    {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }
}
