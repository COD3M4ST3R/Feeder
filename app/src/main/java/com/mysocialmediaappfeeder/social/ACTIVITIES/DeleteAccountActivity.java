package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DeleteAccountActivity extends AppCompatActivity
{

    Button deleteAccount;
    EditText email;
    EditText password;

    String Email;
    String Password;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //  SHARED PREFS
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
        setContentView(R.layout.activity_delete_account);

        deleteAccount = findViewById(R.id.deleteAccount);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //  DELETE BUTTON LISTENER
        deleteAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Email = email.getText().toString();
                Password = password.getText().toString();

                if(Email.isEmpty() || Password.isEmpty())
                {
                    Toast.makeText(DeleteAccountActivity.this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    proceed();
                }
            }
        });
    }


    //  METHOD's

    private void proceed()
    {
        AuthCredential credential = EmailAuthProvider.getCredential(Email, Password);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccountActivity.this);

                    builder.setCancelable(true);
                    builder.setTitle("Delete Account?");
                    builder.setMessage("You can not retrieve your account back after deleting it!");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            //  DELETE ACCOUNT POSTS
                            final Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postOwner").equalTo(currentID);
                            query.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                    {
                                        String delete = dataSnapshot1.getKey();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                                        reference.child(delete).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });

                            //  DELETE ACCOUNT COMMENTS
                            final Query query2 = FirebaseDatabase.getInstance().getReference("Comments").orderByChild("commentatorID").equalTo(currentID);
                            query2.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                    {
                                        String delete = dataSnapshot1.getKey();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments");
                                        reference.child(delete).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });

                            //  DELETE USER FROM COMPLETE FOLLOW SYSTEM FROM ALL USER
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow");
                            reference.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.child(currentID).exists())
                                    {
                                        //  DELETE USER FOLLOWING SEGMENT
                                        if(dataSnapshot.child(currentID).child("following").exists())
                                        {
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(currentID).child("following");
                                            reference1.addListenerForSingleValueEvent(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                {
                                                    final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                                    {
                                                        final String target = dataSnapshot1.getKey();

                                                        //  GO TO Follow > followers > TARGET & DELETE CURRENT USER FROM TARGET's Followers
                                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Follow").child(target).child("followers");
                                                        reference2.child(currentID).removeValue();


                                                        //  UPDATE COUNTER
                                                        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Follow").child(target).child("numberOfFollowers");
                                                        reference3.addListenerForSingleValueEvent(new ValueEventListener()
                                                        {

                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                            {
                                                                int number = dataSnapshot.getValue(int.class);
                                                                number = number - 1;

                                                                FirebaseDatabase.getInstance().getReference().child("Follow").child(target).child("numberOfFollowers").setValue(number);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                                            {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError)
                                                {

                                                }
                                            });
                                        }

                                        //  DELETE USER FOLLOWERS SEGMENT
                                        if(dataSnapshot.child(currentID).child("followers").exists())
                                        {
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(currentID).child("followers");
                                            reference1.addListenerForSingleValueEvent(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                {
                                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                                    {
                                                        final String target = dataSnapshot1.getKey();

                                                        //  GO TO Follow > following > TARGET & DELETE CURRENT USER FROM TARGET's following
                                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Follow").child(target).child("following");
                                                        reference2.child(currentID).removeValue();

                                                        //  UPDATE COUNTER
                                                        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Follow").child(target).child("numberOfFollowing");
                                                        reference3.addListenerForSingleValueEvent(new ValueEventListener()
                                                        {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                            {
                                                                int number = dataSnapshot.getValue(int.class);
                                                                number = number - 1;

                                                                FirebaseDatabase.getInstance().getReference().child("Follow").child(target).child("numberOfFollowing").setValue(number);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                                            {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError)
                                                {

                                                }
                                            });
                                        }
                                    }

                                    else
                                    {

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });



                            //  DELETE REMAINING FOLLOW SEGMENT OF CURRENT
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(currentID);
                            reference1.removeValue();

                            //DELETE POST COUNTER SEGMENT OF CURRENT
                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("PostCounter").child(currentID);
                            reference2.removeValue();


                            //  DELETE ACCOUNT
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                        reference.child(currentID).removeValue();

                                        Intent intent = new Intent(DeleteAccountActivity.this, StartActivity.class);
                                        intent.putExtra("Flag", 1);
                                        startActivity(intent);
                                    }

                                    else
                                    {
                                        Toast.makeText(DeleteAccountActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            // BACKUP
                            final DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            reference3.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.exists())
                                    {
                                        reference3.removeValue();

                                        Intent intent = new Intent(DeleteAccountActivity.this, StartActivity.class);
                                        intent.putExtra("Flag", 1);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });
                        }
                    });
                    builder.show();
                }

                else
                {
                    Toast.makeText(DeleteAccountActivity.this, "E-mail or Password is wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
