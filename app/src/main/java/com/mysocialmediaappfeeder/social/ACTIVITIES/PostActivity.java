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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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

import java.util.HashMap;

public class PostActivity extends AppCompatActivity
{
    // VARIABLES
    View view;
    String text;
    int number = 0;
    String limit = "/300";
    String currentID;
    long date;

    // XML VARIABLES
    ImageButton close;
    Button send;
    ImageButton imageFeed;
    TextView textLimiter;
    EditText EnterText;
    ProgressBar indeterminateBar_Feed;
    ImageView test;

    //  POST VARIABLES
    String myURL = "";
    StorageTask uploadTask;
    Uri resultUri;

    //  FIREBASE
    StorageReference storageReference;

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
        setContentView(R.layout.activity_post);


        //  CASTING OF XML VARIABLES
        close = findViewById(R.id.close);
        send = findViewById(R.id.send);
        imageFeed = findViewById(R.id.imageFeed);
        textLimiter = findViewById(R.id.textLimiter);
        EnterText = findViewById(R.id.EnterText);
        indeterminateBar_Feed = findViewById(R.id.indeterminateBar_Feed);
        test = findViewById(R.id.test);

        indeterminateBar_Feed.setVisibility(View.INVISIBLE);
        send.setVisibility(View.VISIBLE);

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //  GETTING TIME
        date = System.currentTimeMillis();


        //  FIREBASE
        storageReference = FirebaseStorage.getInstance().getReference("Posts");

        currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //  TEXTFEED Watcher
        EnterText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                text = EnterText.getText().toString();
                number = EnterText.length() + 1;

                if(number >= 280)
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

        //  OPEN FEED FRAGMENT
        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_MainActivity();
            }
        });

        //  SEND POST
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                indeterminateBar_Feed.setVisibility(View.VISIBLE);
                uploadPost();
                send.setVisibility(View.INVISIBLE);
            }
        });

        //  CROP IMAGE
        imageFeed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cropImage();
            }
        });

    }


    //  METHODs

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadPost()
    {
        //  TODO --> Add Progress Animation

        //  USER POSTING IMAGEFEED
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

                        send.setVisibility(View.INVISIBLE);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postID = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postID", postID);
                        hashMap.put("postOwner", currentID);
                        hashMap.put("postType", "imageFeed");
                        hashMap.put("postImage", myURL);
                        hashMap.put("description", text);
                        hashMap.put("date", date);

                        reference.child(postID).setValue(hashMap);
                        indeterminateBar_Feed.setVisibility(View.GONE);
                        open_MainActivity();
                        countPost();

                    }else
                    {
                        Toast.makeText(PostActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() 
            {
                @Override
                public void onFailure(@NonNull Exception e) 
                {
                    Toast.makeText(PostActivity.this, "" +  e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else
        {
            if(EnterText.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please enter your feed", Toast.LENGTH_SHORT).show();
            }else
            {
                //  USER POSTING TEXTFEED
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                String postID = reference.push().getKey();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("postID", postID);
                hashMap.put("postOwner", currentID);
                hashMap.put("postType", "textFeed");
                hashMap.put("postImage", "false");
                hashMap.put("description", text);
                hashMap.put("date", date);

                reference.child(postID).setValue(hashMap);
                indeterminateBar_Feed.setVisibility(View.GONE);
                open_MainActivity();
                countPost();
            }
        }
    }


    private void open_MainActivity()
    {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private void cropImage()
    {
        CropImage.activity().setAspectRatio(1,1).start(PostActivity.this);
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
                textChanger();
                test.setImageURI(resultUri);

            } else{

            }
        }
    }


    private void textChanger()
    {
        imageFeed.setVisibility(View.INVISIBLE);
    }


    private void countPost()
    {
        //  UPDATE FEED COUNTER

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("PostCounter");
        reference2.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //  USER POSTED BEFORE; GET THE VALUE AND UPDATE IT WITh +1
                if(dataSnapshot.child(currentID).exists())
                {
                    int counter = dataSnapshot.child(currentID).getValue(int.class);
                    counter = counter + 1;
                    FirebaseDatabase.getInstance().getReference().child("PostCounter").child(currentID).setValue(counter);
                }

                //  IT IS USER's FIRST POST
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("PostCounter").child(currentID).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}






















