package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.SharedPrefs;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;



public class EditActivity extends AppCompatActivity
{
    //  XML VARIABLES
    CircleImageView imageview;
    ImageButton edit_image;
    EditText edit_bio;
    Button edit_save;
    TextView textLimiter;
    EditText edit_FullName;

    //  USER
    User user;

    //  LIMITER
    String limit = "/75";
    int number;

    //  NEW VARIABLES
    String bio;
    String image;
    String fullName;

    //  POST VARIABLES
    String myURL = "";
    StorageTask uploadTask;
    Uri resultUri;

    //  FIREBASE
    StorageReference storageReference;

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
        setContentView(R.layout.activity_edit);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //  FIREBASE
        storageReference = FirebaseStorage.getInstance().getReference("PP");

        //  XML VARIABLES
        imageview = findViewById(R.id.imageview);
        edit_image = findViewById(R.id.edit_image);
        edit_bio = findViewById(R.id.edit_bio);
        edit_save = findViewById(R.id.edit_save);
        textLimiter = findViewById(R.id.textLimiter);
        edit_FullName = findViewById(R.id.edit_FullName);

        //  CURRENT USER ID
        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //  SEARCH FOR CURRENT USER IN DATABASE AND ADD PREVIOUS DATA
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentID);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    user = dataSnapshot.getValue(User.class);

                    //  LOAD PREVIOUS / NEW IMAGE
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(imageview);

                    //  LOAD PREVIOUS BIO
                    edit_bio.setText(user.getBio());

                    //  LOAD PREVIOUS FULLNAME
                    edit_FullName.setText(user.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //  Bio Watcher
        edit_bio.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                bio = edit_bio.getText().toString();
                number = edit_bio.length() + 1;

                if(number >= 70)
                {
                    textLimiter.setText(number + limit);
                    textLimiter.setTextColor(getResources().getColor(R.color.red));
                }

                else
                {
                    textLimiter.setTextColor(getResources().getColor(R.color.grey));
                    textLimiter.setText(number + limit);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });


        edit_FullName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                fullName = edit_FullName.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });


        //  EDIT PICTURE WATCHER
        edit_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cropImage();
            }
        });


        //  SAVE CHANGES
        edit_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //  USER UPDATING PROFILE IMAGE
                if(resultUri != null)
                {
                    final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(resultUri));

                    uploadTask = filereference.putFile(resultUri);

                    uploadTask.continueWithTask(new Continuation()
                    {
                        @Override
                        public Object then(@NonNull Task task) throws Exception
                        {
                            if(!task.isSuccessful())
                            {
                                throw task.getException();
                            }

                            return filereference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if(task.isSuccessful())
                            {
                                Uri downloadUri = task.getResult();
                                myURL = downloadUri.toString();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentID);

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imageURL", ""+myURL);
                                hashMap.put("bio", bio);
                                hashMap.put("fullName", fullName);

                                reference.updateChildren(hashMap);

                                Toast.makeText(EditActivity.this, "Changes has been successfully applied", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                Toast.makeText(EditActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(EditActivity.this, "" +  e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if(resultUri == null)
                {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(currentID);
                    reference1.child("bio").setValue(bio);

                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users").child(currentID);
                    reference2.child("fullName").setValue(fullName);

                    Toast.makeText(EditActivity.this, "Profile has been successfully applied", Toast.LENGTH_SHORT).show();
                }

                // SPLASH SCREEN
                Thread timerThread = new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            sleep(1000);

                        }catch(InterruptedException e)
                        {
                            e.printStackTrace();

                        }finally
                        {
                            finish();
                        }

                    }
                };
                timerThread.start();
            }
        });
    }

    private void cropImage()
    {
        CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(EditActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                resultUri = result.getUri();

                Glide.with(getApplicationContext()).load(resultUri).into(imageview);

            } else{

            }
        }
    }

    //  FILE EXTENSION
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
