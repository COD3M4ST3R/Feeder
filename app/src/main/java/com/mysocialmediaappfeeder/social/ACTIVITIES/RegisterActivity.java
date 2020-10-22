package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{

    // INITIALIZE XML VARIABLES
    EditText editText_Register_FullName;
    EditText editText_Register_UserName;
    EditText editText_Register_Email;
    EditText editText_Register_Password;
    Button Button_Register;
    CheckBox checkBox;
    Button terms;

    // USER CLASS OBJECT
    User user;

    // FIREBASE AUTH
    FirebaseAuth auth;
    DatabaseReference reference;

    // PROGRESS BAR
    ProgressBar indeterminateBar_Register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // CASTING XML VARIABLES
        editText_Register_FullName = findViewById(R.id.editText_Register_FullName);
        editText_Register_UserName = findViewById(R.id.editText_Register_UserName);
        editText_Register_Email = findViewById(R.id.editText_Register_Email);
        editText_Register_Password = findViewById(R.id.editText_Register_Password);
        Button_Register = findViewById(R.id.Button_Register);
        indeterminateBar_Register = findViewById(R.id.indeterminateBar_Register);
        checkBox = findViewById(R.id.checkBox);
        terms = findViewById(R.id.terms);

        // MAKE PROGRESS BAR INVISIBLE
        indeterminateBar_Register.setVisibility(View.INVISIBLE);

        // MAKE REGISTER BUTTON VISIBLE
        Button_Register.setVisibility(View.VISIBLE);

        // CREATING NEW USER
        user = new User();

        // FIREBASE
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");


        //  TERMS CLICK LISTENER
        terms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_TermsActivity();
            }
        });

        // REGISTER USER BUTTON
        Button_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // GETTING INPUTS
                String Register_FullName = editText_Register_FullName.getText().toString();
                final String Register_UserName = editText_Register_UserName.getText().toString();
                String Register_Email = editText_Register_Email.getText().toString();
                String Register_Password = editText_Register_Password.getText().toString();

                user.setFullName(Register_FullName);
                user.setUserName(Register_UserName);
                user.setEmail(Register_Email);
                user.setPassword(Register_Password);


                // CHECKING INPUT LOCALLY
                if(TextUtils.isEmpty(Register_FullName) || TextUtils.isEmpty(Register_UserName) || TextUtils.isEmpty(Register_Email) || TextUtils.isEmpty(Register_Password) || !checkBox.isChecked())
                {
                    Toast.makeText(RegisterActivity.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
                }

                // TODO PASSWORD MATCH LOCAL
                // TODO REGEX
                else if(Register_Password.length() < 8)
                {
                    Toast.makeText(RegisterActivity.this, "Passwords Must Have at Least 8 character", Toast.LENGTH_SHORT).show();
                }

                else if(Register_UserName.length() > 25 || Register_FullName.length() > 25)
                {
                    Toast.makeText(RegisterActivity.this, "Full Name and User Name can not be longer than 25 characters!", Toast.LENGTH_SHORT).show();
                }


                else {

                    //  CHECKING FOR USERNAME IF THERE IS ALREADY ONE WITH SAME USERNAME
                    Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userName").equalTo(Register_UserName);
                    query.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.getChildrenCount() > 0)
                            {
                                Toast.makeText(RegisterActivity.this, "This username has been already taken! Try another one.", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                Register(user.getFullName(), user.getUserName(), user.getEmail(), user.getPassword());
                                // ACTIVATE PROGRESS BAR
                                indeterminateBar_Register.setVisibility(View.VISIBLE);
                                // MAKE REGISTER BUTTON INVISIBLE
                                Button_Register.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
                }
            }
        });
    }

    private void Register(final String FullName, final String UserName, final String Email, final String Password){
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String AuthID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("authID", AuthID);
                    hashMap.put("fullName", FullName);
                    hashMap.put("userName", UserName.toLowerCase());
                    hashMap.put("bio", "Hello There! I am using Feeder.");
                    hashMap.put("imageURL", "https://firebasestorage.googleapis.com/v0/b/project-social-app.appspot.com/o/placeholdersmall.jpg?alt=media&token=837d74b6-9ecf-413c-a0f7-8b9460a9bf46");
                    hashMap.put("accountType", "public");

                    //  Push Generate Key Like "-MHJ...."
                    reference.child(AuthID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                indeterminateBar_Register.setVisibility(View.GONE);
                                open_LoginActivity();
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            } else {
                                indeterminateBar_Register.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "DATA FLOW FAILURE", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    indeterminateBar_Register.setVisibility(View.GONE);
                    // MAKE REGISTER BUTTON VISIBLE
                    Button_Register.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterActivity.this, "AUTH FAILURE", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    /*
    private void Register(final String Register_FullName, final String Register_UserName, String Register_Email, String Register_Password){
        auth.createUserWithEmailAndPassword(Register_Email, Register_Password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").push();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("fullName", Register_FullName);
                    hashMap.put("userName", Register_UserName.toLowerCase());
                    hashMap.put("bio", "");
                    hashMap.put("imageURL", "https://firebasestorage.googleapis.com/v0/b/project-social-app.appspot.com/o/placeholdersmall.jpg?alt=media&token=837d74b6-9ecf-413c-a0f7-8b9460a9bf46");

                    reference .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                open_LoginActivity();
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else{
                    Toast.makeText(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    */

    public void open_LoginActivity(){

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void open_TermsActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, TermsActivity.class);
        startActivity(intent);
    }
}
