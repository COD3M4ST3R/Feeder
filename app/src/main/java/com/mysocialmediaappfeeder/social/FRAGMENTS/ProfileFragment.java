package com.mysocialmediaappfeeder.social.FRAGMENTS;



import android.content.Context;
import android.content.Intent;
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
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.ACTIVITIES.EditActivity;
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
import java.util.List;


public class ProfileFragment extends Fragment
{
    //  VARIABLES
    View view;
    int followers = 0;
    int following = 0;

    //  INITIALIZE ADAPTER
    ProfileAdapter profileAdapter;

    //  DATASET
    List<Posts> mData;

    //  STRINGS OF BUNDLE
    String fullName;
    String userName;
    String displayName;
    String bio;

    String AuthID;

    //  INITIALIZING XML VARIABLES OF "fragment_profile.xml"
    ImageButton Profile_ProfileButton;
    TextView Profile_Bio;
    TextView Profile_DisplayName;
    TextView Profile_FeedNumber;
    TextView Profile_FollowersNumber;
    TextView Profile_FollowingNumber;
    Button Profile_FeedButton;
    Button Profile_FollowersButton;
    Button Profile_FollowingButton;
    ImageView Profile_CircleImage;
    RecyclerView show;
    NestedScrollView scrollView; //TODO
    ImageButton Profile_Edit;
    Button click;
    ImageView circle;

    int lastPos3;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //  CASTING XML VARIABLES OF "fragment_profile.xml"
        Profile_ProfileButton = view.findViewById(R.id.Profile_ProfileButton);
        Profile_Bio = view.findViewById(R.id.Profile_Bio);
        Profile_DisplayName = view.findViewById(R.id.Profile_DisplayName);
        Profile_FeedNumber = view.findViewById(R.id.Profile_FeedNumber);
        Profile_FollowersNumber = view.findViewById(R.id.Profile_FollowersNumber);
        Profile_FollowingNumber = view.findViewById(R.id.Profile_FollowingNumber);
        Profile_FeedButton = view.findViewById(R.id.Profile_FeedButton);
        Profile_FollowersButton = view.findViewById(R.id.Profile_FollowersButton);
        Profile_FollowingButton = view.findViewById(R.id.Profile_FollowingButton);
        Profile_CircleImage = view.findViewById(R.id.Profile_CircleImage);
        scrollView = view.findViewById(R.id.scrollView);
        show = view.findViewById(R.id.show);
        Profile_Edit = view.findViewById(R.id.Profile_Edit);
        click = view.findViewById(R.id.click);
        circle = view.findViewById(R.id.circle);

        //  CIRCLE ANIMATION
        AnimationDrawable animationDrawable =  (AnimationDrawable) circle.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();


        show.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //show.setLayoutManager(new LinearLayoutManager(getActivity()));
        show.setLayoutManager(linearLayoutManager);
        profileAdapter = new ProfileAdapter(getContext(), mData);
        show.setAdapter(profileAdapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Users");
        final FirebaseUser user = auth.getCurrentUser();
        AuthID = user.getUid(); // == currentID

        //  USER INFO DISPLAYING IN PROFILE
        reference.child(AuthID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageURL()).into(Profile_CircleImage);
                Profile_Bio.setText(user.getBio());
                fullName = user.getFullName();
                userName = user.getUserName();
                displayName = fullName + "  @" + userName;
                Profile_DisplayName.setText(displayName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(getContext(), "ERROR getting Profile", Toast.LENGTH_SHORT).show();
            }
        });


        //  USER EDIT PROFILE
        Profile_Edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_EditActivity();
            }
        });


        //  CHECKING IF USER HAS AT LEAST 1 FOLLOW 1 PERSON OR FOLLOWED BY 1 PERSON
        //  FOLLOW INFO DISPLAYING IN PROFILE
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Follow");
        reference2.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(AuthID))
                {
                    //  USER AT LEAST FOLLOW 1 PERSON OR FOLLOWED BY 1 PERSON
                    displayFollowInfo(AuthID);
                }

                else
                {
                    following = 0;
                    followers = 0;
                    Profile_FollowingNumber.setText(String.valueOf(following));
                    Profile_FollowersNumber.setText(String.valueOf(followers));

                    //  MAKE FOLLOW INFO BUTTONS INVISIBLE for METHOD "displayFollowList()"
                    Profile_FollowersButton.setVisibility(View.INVISIBLE);
                    Profile_FollowingButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


        //  BIGGER PROFILE PICTURE
        Profile_ProfileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_BiggerProfileImage(v);
            }
        });

        //  SCROLLTOTOP
        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext())
        {
            @Override protected int getVerticalSnapPreference()
            {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        ////////////////////////////////////////////////////////////////////////////////////////////


        displayFollowList();
        postCounter();
        displayPosts();


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
                int check = sharedPref.getInt("lastPos3", 0);

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

                lastPos3 = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            }
        });


        return view;
    }



    //  METHODs


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
        editor.putInt("lastPos3", lastPos3);
        editor.apply();
    }

    private void displayFollowInfo(String ID)
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

                    Profile_FollowersNumber.setText(String.valueOf(followers));
                }

                else
                    followers = 0;
                    Profile_FollowersNumber.setText(String.valueOf(followers));
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
                    Profile_FollowingNumber.setText(String.valueOf(following));
                }

                else
                {
                    following = 0;
                    Profile_FollowingNumber.setText(String.valueOf(following));
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
        Profile_FollowersButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String command = "list_current_followers";

                //  Bundle to send current user AuthID & command
                Bundle data = new Bundle();
                data.putString("currentID", AuthID);
                data.putString("command", command);
                data.putString("userName", userName);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FollowFragment followFragment = new FollowFragment();

                followFragment.setArguments(data);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, followFragment).addToBackStack(null).commit();

            }
        });

        //  Following Button onClick sends to Fragment "FollowFragment.java"
        Profile_FollowingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String command = "list_current_following";

                //  Bundle to send current user AuthID & command
                Bundle data = new Bundle();
                data.putString("currentID", AuthID);
                data.putString("command", command);
                data.putString("userName", userName);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FollowFragment followFragment = new FollowFragment();

                followFragment.setArguments(data);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, followFragment).addToBackStack(null).commit();
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
                if(dataSnapshot.child(AuthID).exists())
                {
                    int counter;
                    counter = dataSnapshot.child(AuthID).getValue(int.class);
                    Profile_FeedNumber.setText(String.valueOf(counter));
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
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postOwner").equalTo(AuthID);
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

    private void open_EditActivity()
    {
        Intent intent = new Intent(getActivity(), EditActivity.class);
        startActivity(intent);
    }

    private void open_BiggerProfileImage(final View v)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageURL");
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String image = snapshot.getValue(String.class);

                Bundle data = new Bundle();
                data.putString("image", image);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                BiggerProfileImage biggerProfileImage = new BiggerProfileImage();

                biggerProfileImage.setArguments(data);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, biggerProfileImage).addToBackStack(null).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}
