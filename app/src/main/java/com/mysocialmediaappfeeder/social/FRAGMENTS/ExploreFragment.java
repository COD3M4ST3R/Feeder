package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.mysocialmediaappfeeder.social.ADAPTERS.MainAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.Posts;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ExploreFragment extends Fragment
{
    //  VIEW
    View view;

    //  DATASET
    List<Posts> mData;

    //   INITIALIZING XML VARIABLES
    RecyclerView show;
    SwipeRefreshLayout swiperefresh;
    Button click;

    // INITIALIZING ADAPTER
    MainAdapter mainAdapter;

    //  TIME
    long currentTime;

    //  TIME LIMIT SETTED TO 1 DAY (24 HOUR)
    long limit = 86400000;
    long epox;

    int lastPos2;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_explore, container, false);

        //  CASTING XML VARIABLES
        show = view.findViewById(R.id.show);
        swiperefresh = view.findViewById(R.id.swiperefresh);
        click = view.findViewById(R.id.click);

        mData.clear();

        //  CONST
        MainAdapter.MainAdapterInterface mainAdapterInterface = new MainAdapter.MainAdapterInterface()
        {
            @Override
            public void OnItemClicked(int pos, int flag)
            {
                if(flag == 1)
                {
                    show.smoothScrollToPosition(pos + 1);
                }

                if(flag == 2)
                {
                    show.smoothScrollToPosition(pos + 1);
                }
            }
        };

        //  GETTING TIME
        currentTime = System.currentTimeMillis();
        epox = currentTime - limit;

        //  SWIPE TO REFRESH
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ExploreFragment exploreFragment = new ExploreFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, exploreFragment).addToBackStack(null).commit();
            }
        });

        show.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        show.setLayoutManager(linearLayoutManager);

        mainAdapter = new MainAdapter(getContext(), mData, mainAdapterInterface);
        show.setAdapter(mainAdapter);


        retrieveData();


        //  SMOOTH SCROLL
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
                int check = sharedPref.getInt("lastPos2", 0);

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

                lastPos2 = linearLayoutManager.findLastCompletelyVisibleItemPosition();
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

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lastPos2", lastPos2);
        editor.apply();
    }

    private void retrieveData()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Posts");
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int counter = 0;

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(dataSnapshot1.child("date").getValue(long.class) > epox )
                    {
                        Posts posts = dataSnapshot1.getValue(Posts.class);

                        mData.add(posts);
                        counter = counter + 1;

                        if(counter % 3 == 0)
                        {
                            //  FOOOOOOORRR THE GLORIOUSSSSSSSSSS ADMOBBBBBBBBBBBB
                            mData.add(null);
                            mainAdapter.notifyDataSetChanged();
                        }
                    }


                }
                itsSortTime(mData.size());
                mainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void itsSortTime(int size)  // BUBBLE
    {
        int n = size;

        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n-i-1; j++)
            {
                if(mData.get(j) != null && mData.get(i) != null)
                {
                    if(mData.get(j + 1) != null)
                    {
                        likeTurner(mData.get(j), mData.get(j + 1), j);
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

    private void likeTurner(final Posts post, final Posts post2, final int j)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LikeCounter").child(post.getPostID());
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                final int likes;

                if(dataSnapshot.exists())
                {
                    likes = dataSnapshot.getValue(int.class);
                }

                else
                {
                    likes = 0;
                }

                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("LikeCounter").child(post2.getPostID());
                reference2.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        final int likes2;

                        if(dataSnapshot.exists())
                        {
                            likes2 = dataSnapshot.getValue(int.class);
                        }

                        else
                        {
                            likes2 = 0;
                        }

                        if(likes2 > likes)
                        {
                            Collections.swap(mData, j, j + 1);
                            mainAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
