package com.mysocialmediaappfeeder.social.ACTIVITIES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.FRAGMENTS.DiscoverFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.ExploreFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.FeedFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.InfoFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.LogoutFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.NotificationFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.ProfileFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.ShareFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.TodaysQuestion;
import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.SharedPrefs;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // INITIALIZE VARIABLES
    private DrawerLayout drawer;

    //  INITIALIZING XML VARIABLES OF "nav_header.xml"
    ConstraintLayout ProfileButton;
    TextView fullName;
    TextView userName;
    ImageView Profile_CircleImage;
    Button notificationButton;
    ImageView circle;
    Button question;

    //  XML
    ImageButton feed;

    //  FIREBASE
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    DatabaseReference reference;
    String AuthID;

    SharedPrefs sharedPrefs;

    String at = "@";


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
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  TODO --> add custom made toolbar "feeder" drawable
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //  HIDE STATUS BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //  CREATING VIEW TO REACH HEADER VIEW
        View headerView = navigationView.getHeaderView(0);

        //  CASTING XML VARIABLES OF "nav_header.xml"/
        ProfileButton = headerView.findViewById(R.id.ProfileButton);
        fullName = headerView.findViewById(R.id.fullName);
        userName = headerView.findViewById(R.id.userName);
        Profile_CircleImage = headerView.findViewById(R.id.Profile_CircleImage);
        notificationButton = headerView.findViewById(R.id.notificationButton);
        circle = headerView.findViewById(R.id.circle);
        question = headerView.findViewById(R.id.question);

        AnimationDrawable animationDrawable =  (AnimationDrawable) circle.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        //  XML
        feed = findViewById(R.id.feed);


        if(sharedPrefs.loadNightMode() == true)
        {
            setTheme(R.style.DarkMode);
        }

        else
        {
            setTheme(R.style.AppTheme);
        }

        //  POST FEED
        feed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_PostActivity();
            }
        });

        //  PROFILE BUTTON ONCLICK TO PROFILE FRAGMENT
        ProfileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //  Replacing current fragment with profile fragment in container.
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        //  DISPLAY NOTIFICATIONS
        notificationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //  OPEN NOTIFICATION FRAGMENT
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //  TODAY'S QUESTION
        question.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //  OPEN TODAYSQUESTION FRAGMENT
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TodaysQuestion()).commit();
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        //  DISPLAYING HEADER WITH CURRENT LOGGED IN USER
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Users");
        FirebaseUser user = auth.getCurrentUser();
        AuthID = user.getUid();


        reference.child(AuthID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(Profile_CircleImage);
                    fullName.setText(user.getFullName());
                    userName.setText((at + user.getUserName()));
                }

                else
                {
                    open_StartActivity();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////


        // FIRST SCREEN
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_feed);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.nav_feed:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment()).commit();
                break;

            case R.id.nav_discover:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExploreFragment()).commit();
                break;

            case  R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DiscoverFragment()).commit();
                break;

            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_share:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
                break;

            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InfoFragment()).commit();
                break;

            case R.id.nav_logout:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LogoutFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed ();
        }
    }

    private void open_StartActivity()
    {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
    }

    private void open_PostActivity()
    {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
    }
}
