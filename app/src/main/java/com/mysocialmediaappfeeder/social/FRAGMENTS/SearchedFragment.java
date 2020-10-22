package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.ADAPTERS.ProfileAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.Posts;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class SearchedFragment extends Fragment
{
    View view;
    String operatorADD = "operatorADD";
    String operatorREMOVE = "operatorREMOVE";
    int following;
    int followers;

    //  INITIALIZING XML VARIABLES of "searchedFragment.xml"
    ImageButton SearchedProfile_ProfileButton;
    TextView SearchedProfile_DisplayName;
    TextView SearchedProfile_Bio;
    Button Button_Follow;
    Button Button_Feed;
    Button Button_FeedIndicator;
    TextView SearchedProfile_FeedNumber;
    TextView SearchedProfile_FollowersNumber;
    TextView SearchedProfile_FollowingNumber;
    Button SearchedProfile_FeedButton;
    Button SearchedProfile_FollowersButton;
    Button SearchedProfile_FollowingButton;
    ImageView Profile_CircleImage;
    RecyclerView show;
    Button click;
    NestedScrollView scrollView;
    ImageView circle;
    ImageView locked_symbol;
    TextView locked_text;

    //  ADAPTER VARIABLE
    ProfileAdapter profileAdapter;

    //  DATASET
    List<Posts> mData;

    //  FIREBASE
    FirebaseUser firebaseUser;

    //  VARIABLES OF BUNDLE
    String fullName;
    String userName;
    String displayName;
    String bio;
    String imageURL;
    String authID;
    String accountType;

    //  PULSE
    ImageView pulse_1;
    ImageView pulse_2;
    Handler handler;

    int lastPos;
    int flag = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_searched, container, false);

        //  GETTING CURRENT USER TO IDENTIFY "AuthID" to reach Follow list
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //  BUNDLE COMING FROM SEARCHADAPTER
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            fullName = bundle.getString("fullName");
            userName = bundle.getString("userName");
            bio = bundle.getString("bio");
            imageURL = bundle.getString("imageURL");
            authID = bundle.getString("authID");  //    TARGET ID
            accountType = bundle.getString("accountType");
        }

        //   CASTING VARIABLES of "searchedFragment.xml"
        SearchedProfile_ProfileButton = view.findViewById(R.id.SearchedProfile_ProfileButton);
        SearchedProfile_DisplayName = view.findViewById(R.id.SearchedProfile_DisplayName);
        SearchedProfile_Bio  = view.findViewById(R.id.SearchedProfile_Bio);
        Button_Follow = view.findViewById(R.id.Button_Follow);
        Button_Feed = view.findViewById(R.id.Button_Feed);
        Button_FeedIndicator = view.findViewById(R.id.Button_FeedIndicator);
        SearchedProfile_FeedNumber = view.findViewById(R.id.SearchedProfile_FeedNumber);
        SearchedProfile_FollowersNumber = view.findViewById(R.id.SearchedProfile_FollowersNumber);
        SearchedProfile_FollowingNumber = view.findViewById(R.id.SearchedProfile_FollowingNumber);
        SearchedProfile_FeedButton = view.findViewById(R.id.SearchedProfile_FeedButton);
        SearchedProfile_FollowersButton = view.findViewById(R.id.SearchedProfile_FollowersButton);
        SearchedProfile_FollowingButton = view.findViewById(R.id.SearchedProfile_FollowingButton);
        Profile_CircleImage = view.findViewById(R.id.Profile_CircleImage);
        show = view.findViewById(R.id.show);
        click = view.findViewById(R.id.click);
        scrollView = view.findViewById(R.id.scrollView);
        circle = view.findViewById(R.id.circle);
        locked_symbol = view.findViewById(R.id.locked_symbol);
        locked_text = view.findViewById(R.id.locked_text);
        pulse_1 = view.findViewById(R.id.pulse_1);
        pulse_2 = view.findViewById(R.id.pulse_2);


        //  GENERAL VISIBILITY CHECK
        locked_symbol.setVisibility(View.INVISIBLE);
        locked_text.setVisibility(View.INVISIBLE);

        //  CIRCLE ANIMATION
        AnimationDrawable animationDrawable =  (AnimationDrawable) circle.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        //  CASTING ADAPTER TO RECYCLERVIEW "show"
        show.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //show.setLayoutManager(new LinearLayoutManager(getActivity()));
        show.setLayoutManager(linearLayoutManager);
        profileAdapter = new ProfileAdapter(getContext(),mData);
        show.setAdapter(profileAdapter);


        displayName = fullName + "  @" + userName;

        Glide.with(getContext()).load(imageURL).into(Profile_CircleImage);
        SearchedProfile_DisplayName.setText(displayName);
        SearchedProfile_Bio.setText(bio);

        //  CHECK IF USER SEARCHED HIMSELF
        if(authID.equals(firebaseUser.getUid()))
        {
            Button_Follow.setVisibility(View.INVISIBLE);
            Button_Feed.setVisibility(View.INVISIBLE);
            Button_FeedIndicator.setVisibility(View.INVISIBLE);
            flag = 1;
        }else{
            Button_Follow.setVisibility(View.VISIBLE);
        }

        //Button_Feed.setVisibility(View.INVISIBLE);
        Button_FeedIndicator.setVisibility(View.INVISIBLE);


        //  BIGGER PROFILE PICTURE
        SearchedProfile_ProfileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_BiggerProfileImageFragment(imageURL, view);
            }
        });



        //  SMOOTHSCROLL
        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext())
        {
            @Override protected int getVerticalSnapPreference()
            {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        //  AUTO SCROLL AFTER CLOSING COMMENT
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                click.performClick();
            }
        }, 500);

        click.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int check = sharedPref.getInt("lastPos", 0);

                if(check != 0 && check < mData.size() && check > 0)
                {
                    smoothScroller.setTargetPosition(check);
                    linearLayoutManager.startSmoothScroll(smoothScroller);
                }

                if(check == -1)
                {
                    smoothScroller.setTargetPosition(mData.size());
                    linearLayoutManager.startSmoothScroll(smoothScroller);
                }
            }
        });

        //  Last Position Finder
        show.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                lastPos = linearLayoutManager.findLastVisibleItemPosition();
            }
        });





        isFollowing(authID, Button_Follow);
        displayFollowInfo(authID);
        postCounter();


        //  FOLLOW ACTION (CURRENT USER PRESSING BUTTON TO FOLLOW TARGET USER)
        Button_Follow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Button_Follow.getText().toString().equals("FOLLOW")) //  LET HIM FOLLOW THAT USER
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(authID).setValue("feed");
                    Button_FeedIndicator.setVisibility(View.VISIBLE);
                    startTask();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(authID).child("followers").child(firebaseUser.getUid()).setValue("true");

                    //  GIVE FOLLOWED USER NOTIFICATION
                    notificationFollow(authID);

                    followOperator(operatorADD, firebaseUser.getUid(), authID); //  This function with this parameters will create "numberOfFollowing" field for
                    // "firebaseUser.getUid()" if it is already exits it will read integer value and update it with +1. ALSO This function with this parameters will
                    // create "numberOfFollowers" field for "authID" if it is already exist it will read integer value and update it with +1.

                }else   //  LET HIM UNFOLLOW THAT USER
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(authID).removeValue();
                    Button_FeedIndicator.setVisibility(View.GONE);
                    stopTask();
                    //Button_Feed.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(authID).child("followers").child(firebaseUser.getUid()).removeValue();

                    followOperator(operatorREMOVE, firebaseUser.getUid(), authID);
                }
            }
        });

        openFeedAgain();


        //  USER STILL FOLLOWS THE TARGET ID BUT DOES NOT WANT TARGET IN HIS FEED
        Button_FeedIndicator.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(authID).setValue("true");
                Button_FeedIndicator.setVisibility(View.INVISIBLE);
                stopTask();
                //Button_Feed.setVisibility(View.VISIBLE);
            }
        });


        //  FOLLOW INFO DISPLAYING IN PROFILE
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Follow");
        reference2.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(authID))
                {
                    //  USER AT LEAST FOLLOW 1 PERSON OR FOLLOWED BY 1 PERSON
                    displayFollowInfo(authID);
                }

                else
                {
                    following = 0;
                    followers = 0;
                    SearchedProfile_FollowingNumber.setText(String.valueOf(following));
                    SearchedProfile_FollowersNumber.setText(String.valueOf(followers));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


        displayFollowList();


        if(accountType.equals("private"))
        {
            if(flag == 1)
            {
                displayPosts();
            }

            else
            {
                //  Display targetID posts;  if currentID has the targetID in his own followers list
                String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(currentID).child("followers").child(authID);
                reference.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            //  currentID can see profile of targetID
                            displayPosts();
                        }

                        else
                        {
                            //  currentID cannot see profile of targetID
                            locked_symbol.setVisibility(View.VISIBLE);
                            locked_text.setVisibility(View.VISIBLE);

                            SearchedProfile_FollowingButton.setVisibility(View.INVISIBLE);
                            SearchedProfile_FollowersButton.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
            }
        }

        else
        {
            displayPosts();
        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////  PULSE
        handler = new Handler();


        return view;
    }


    private void startTask()
    {
        this.runnable.run();
    }

    private void stopTask()
    {
        pulse_1.setVisibility(View.INVISIBLE);
        pulse_2.setVisibility(View.INVISIBLE);
    }

    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            pulse_1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction(new Runnable()
            {
                @Override
                public void run()
                {
                    pulse_1.setScaleX(1f);
                    pulse_1.setScaleY(1f);
                    pulse_1.setAlpha(1f);
                }
            });

            pulse_2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700).withEndAction(new Runnable()
            {
                @Override
                public void run()
                {
                    pulse_2.setScaleX(1f);
                    pulse_2.setScaleY(1f);
                    pulse_2.setAlpha(1f);
                }
            });

            handler.postDelayed(runnable, 1500);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mData = new ArrayList<>();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lastPos", lastPos);
        editor.apply();
    }

    //  "isFollowing" METHOD TO CHECK DATABASE FOLLOWING FOR IMPLEMENTING THE BUTTONS
    private void isFollowing(final String targetID, final Button Button_Follow)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(targetID).exists())
                {
                    //  USER ALREADY FOLLOWING THE TARGET ID
                    Button_Follow.setText("FOLLOWING");
                    //Button_Feed.setVisibility(View.VISIBLE);

                    if("feed".equals(dataSnapshot.child(targetID).getValue(String.class)))
                    {
                        //  USER FOLLOWS AND FEED IS OPEN FOR TARGET ID
                        //Button_Feed.setVisibility(View.INVISIBLE);
                        Button_FeedIndicator.setVisibility(View.VISIBLE);
                        startTask();

                    } else
                    {
                        //  USER FOLLOW TARGET BUT CLOSED FEED HERE ALREADY
                        openFeedAgain();
                    }

                }else
                {
                    //  USER NOT FOLLOWING TARGET ID
                    Button_Follow.setText("FOLLOW");
                    //Button_Feed.setVisibility(View.INVISIBLE);
                    Button_FeedIndicator.setVisibility(View.INVISIBLE);
                    stopTask();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void followOperator(final String command, final String currentID, final String targetID)
    {
        String operatorADD = "operatorADD";
        String operatorREMOVE = "operatorREMOVE";

        if(command.equals(operatorADD))
        {
            //  TODO --> CHECK CURRENT USER's numberOfFollowing field empty or not. If empty create that field with value 1 ELSE get value and update it with +1
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID);
            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child("numberOfFollowing").exists())
                    {
                        //  READ INTEGER VALUE AND UPDATE
                        int number = dataSnapshot.child("numberOfFollowing").getValue(int.class);
                        number = number + 1;

                        //  WRITE UPDATED DATA
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID).child("numberOfFollowing").setValue(number);
                    }

                    else
                    {
                        //  CREATE NEW CHILD as "numberOfFollowing with integer value 1"
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID).child("numberOfFollowing").setValue(1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });

            //  TODO --> CHECK TARGET USER's numberOfFollowers field empty or not. If empty create that field with value 1 ELSE get value and update it with +1
            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Follow").child(targetID);
            reference2.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child("numberOfFollowers").exists())
                    {
                        //  READ INTEGER VALUE AND UPDATE
                        int number = dataSnapshot.child("numberOfFollowers").getValue(int.class);
                        number = number + 1;

                        //  WRITE UPDATE DATA
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(targetID).child("numberOfFollowers").setValue(number);
                    }

                    else
                    {
                        //  CREATE NEW CHILD as "numberOfFollowers with integer value 1"
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(targetID).child("numberOfFollowers").setValue(1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }

        else if(command.equals(operatorREMOVE))
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID);
            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child("numberOfFollowing").exists())
                    {
                        int number = dataSnapshot.child("numberOfFollowing").getValue(int.class);
                        number = number - 1;

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID).child("numberOfFollowing").setValue(number);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });

            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Follow").child(targetID);
            reference2.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child("numberOfFollowers").exists())
                    {
                        int number = dataSnapshot.child("numberOfFollowers").getValue(int.class);
                        number = number - 1;

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(targetID).child("numberOfFollowers").setValue(number);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
    }

    private void displayFollowInfo(final String ID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(ID);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("numberOfFollowers").exists())
                {
                    followers = dataSnapshot.child("numberOfFollowers").getValue(int.class);
                    SearchedProfile_FollowersNumber.setText(String.valueOf(followers));
                }

                else
                    followers = 0;
                SearchedProfile_FollowersNumber.setText(String.valueOf(followers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Follow").child(ID);
        reference2.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("numberOfFollowing").exists())
                {
                    following = dataSnapshot.child("numberOfFollowing").getValue(int.class);
                    SearchedProfile_FollowingNumber.setText(String.valueOf(following));
                }

                else
                {
                    following = 0;
                    SearchedProfile_FollowingNumber.setText(String.valueOf(following));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

    private void displayFollowList()
    {
        //  Followers Button onClick sends to Fragment "FollowFragment.java"
        SearchedProfile_FollowersButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String command = "list_searched_followers";

                //  Bundle to send current user AuthID & command
                Bundle data = new Bundle();
                data.putString("currentID", authID);    // authID is the searchedID here
                data.putString("command", command);
                data.putString("userName", userName);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FollowFragment followFragment = new FollowFragment();

                followFragment.setArguments(data);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, followFragment).addToBackStack(null).commit();
            }
        });

        //  Following Button onClick sends to Fragment "FollowFragment.java"
        SearchedProfile_FollowingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String command = "list_searched_following";

                //  Bundle to send current user AuthID & command
                Bundle data = new Bundle();
                data.putString("currentID", authID);    // authID is the searchedID here
                data.putString("command", command);
                data.putString("userName", userName);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FollowFragment followFragment = new FollowFragment();

                followFragment.setArguments(data);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, followFragment).addToBackStack(null).commit();
            }
        });
    }

    private void openFeedAgain()
    {
        //  USER FOLLOW TARGET ID BUT CLOSED FEED. NOW CURRENT ID WANTS TO ADD TARGET ID TO FEED AGAIN

        Button_Feed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(authID).setValue("feed");
                startTask();
            }
        });

    }

    private void postCounter()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PostCounter");
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(authID).exists())
                {
                    int counter;
                    counter = dataSnapshot.child(authID).getValue(int.class);
                    SearchedProfile_FeedNumber.setText(String.valueOf(counter));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void displayPosts()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postOwner").equalTo(authID);
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mData.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Posts posts = dataSnapshot1.getValue(Posts.class);
                    mData.add(posts);
                }
                itsSortTime(mData.size());
                profileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void itsSortTime(int size)
    {
        int n = size;

        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (mData.get(j).getDate() < mData.get(j + 1).getDate())
                {
                    Collections.swap(mData, j, j + 1);
                    profileAdapter.notifyDataSetChanged();
                }

    }

    private void notificationFollow(String authID)
    {
        //  targetID need to get notification
        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String targetID = authID;
        long date = System.currentTimeMillis();

        String type = "FOLLOW";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(targetID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderID", currentID);
        hashMap.put("receiverID", targetID);
        hashMap.put("date", date);
        hashMap.put("type", type);

        reference.push().setValue(hashMap);
    }

    private void open_BiggerProfileImageFragment(String image, View v)
    {
        Bundle data = new Bundle();
        data.putString("image", image);

        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        BiggerProfileImage biggerProfileImage = new BiggerProfileImage();

        biggerProfileImage.setArguments(data);

        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, biggerProfileImage).addToBackStack(null).commit();
    }
}
