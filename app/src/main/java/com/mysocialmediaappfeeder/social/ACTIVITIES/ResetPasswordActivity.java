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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity
{
    //  XML
    EditText email;
    Button send;

    String e;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // XML
        email = findViewById(R.id.email);
        send = findViewById(R.id.send);

        final FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();

        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                e = email.getText().toString();

                if(!e.isEmpty())
                {
                    firebaseAuth.sendPasswordResetEmail(e).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(ResetPasswordActivity.this, "Please check your E-Mail for password change", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ResetPasswordActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else
                {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter your E-Mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
