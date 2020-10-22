package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.mysocialmediaappfeeder.social.ADAPTERS.MainAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.Posts;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FeedFragment extends Fragment
{
    //  VARIABLES
    View view;
    private String currentID;
    private String targetID;


    //  INITIALIZING XML VARIABLES of "fragment_feed.xml"
    private RecyclerView Feed_PostList;
    private LinearLayout scrollToTop;
    private TextView welcome;
    private SwipeRefreshLayout swiperefresh;
    Button click;

    //  DATASET
    private List<Posts> mData;

    //  ADAPTER
    private MainAdapter mainAdapter;

    int lastPos;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_feed, container, false);


        mData.clear();


        //  CASTING XML VARIABLES of "fragment_feed.xml"
        Feed_PostList = view.findViewById(R.id.Feed_PostList);
        scrollToTop = view.findViewById(R.id.scrollToTop);
        welcome = view.findViewById(R.id.welcome);
        swiperefresh = view.findViewById(R.id.swiperefresh);
        click = view.findViewById(R.id.click);

        //  currentID
        currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //  CONST
        MainAdapter.MainAdapterInterface mainAdapterInterface = new MainAdapter.MainAdapterInterface()
        {
            @Override
            public void OnItemClicked(int pos, int flag)
            {
                if(flag == 1)
                {
                    Feed_PostList.smoothScrollToPosition(pos + 1);
                }

                if(flag == 2)
                {
                    Feed_PostList.smoothScrollToPosition(pos + 1);
                }
            }
        };


        Feed_PostList.setHasFixedSize(true);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        Feed_PostList.setLayoutManager(linearLayoutManager);

        /*
        linearLayoutManager2 = new LinearLayoutManager(getActivity());
        Feed_PostList.setLayoutManager(linearLayoutManager2);
        */

        mainAdapter = new MainAdapter(getContext(), mData, mainAdapterInterface);
        Feed_PostList.setAdapter(mainAdapter);
        Feed_PostList.setItemAnimator(null);


        //  SCROLL TO TOP
        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext())
        {
            @Override protected int getVerticalSnapPreference()
            {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        /*
        smoothScroller2 = new LinearSmoothScroller(getContext())
        {
            @Override protected int getVerticalSnapPreference()
            {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        */


        scrollToTop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                smoothScroller.setTargetPosition(0);
                linearLayoutManager.startSmoothScroll(smoothScroller);
            }
        });



        newUserCheck();

        getPosts();


        //  AUTO SCROLL AFTER CLOSING COMMENT
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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


        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FeedFragment feedFragment = new FeedFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, feedFragment).addToBackStack(null).commit();
            }
        });

        Feed_PostList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                lastPos = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mData = new ArrayList<>();
    }


    //  ONLY GET FOLLOWING --> FEED users post
    private void getPosts()
    {

        //  TODO UNDER CONSTRUCTION
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Follow");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //  USER IS FOLLOWING OR GETTING FOLLOWING FROM AT LEAST 1 USER
                if (dataSnapshot.child(currentID).exists())
                {
                    //  USER IS FOLLOWING AT LEAST 1 USER
                    if (dataSnapshot.child(currentID).child("following").exists())
                    {
                        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Follow").child(currentID).child("following");
                        reference3.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                for(final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                {
                                    if(dataSnapshot1.getValue(String.class).equals("feed"))
                                    {

                                        //  IS USER THAT OPENED FEED FROM CURRENT USER EXIST IN AUTH?
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(dataSnapshot1.getKey());
                                        reference.addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                if(dataSnapshot.exists())
                                                {
                                                    targetID = dataSnapshot1.getKey();// async?????????
                                                    retrieveData(targetID);
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
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveData(final String targetID)
    {
            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postOwner").equalTo(targetID).limitToLast(3);
            query.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        //  TODO --> We can simply add here milisecond from calendar to limit getting data by time but what about showing them with TimeStamp?
                        Posts posts = dataSnapshot1.getValue(Posts.class);
                        mData.add(posts);
                    }
                    mData.add(null);
                    itsSortTime(mData.size());
                    mainAdapter.notifyDataSetChanged();
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
            {
                if(mData.get(j) != null && mData.get(i) != null)
                {
                    if(mData.get(j + 1) != null)
                    {
                        if (mData.get(j).getDate() < mData.get(j + 1).getDate()) {
                            Collections.swap(mData, j, j + 1);
                            mainAdapter.notifyDataSetChanged();
                        }
                    }
                }

                else
                {
                    if(mData.get(i) == null)
                    {
                        i++;
                    }

                    if(mData.get(j) == null)
                    {
                        j++;
                    }
                }
            }
    }

    private void newUserCheck()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(currentID);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("following").exists())
                {
                    welcome.setVisibility(View.INVISIBLE);
                }

                else
                {
                    welcome.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
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

    private void scrollBack(LinearLayoutManager linearLayoutManager, RecyclerView.SmoothScroller smoothScroller, int pos)
    {
        smoothScroller.setTargetPosition(pos);
        linearLayoutManager.startSmoothScroll(smoothScroller);
    }


}

