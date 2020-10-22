package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mysocialmediaappfeeder.social.ADAPTERS.SearchAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;



public class FollowFragment extends Fragment
{
    //  VARIABLES
    String command;
    String currentID;
    String userName;

    //  INITIALIZING XML VARIABLES of "fragment_follow.xml"
    TextView followList_Name;
    RecyclerView followList;

    //  INITIALIZING VIEW
    View view;

    //  INITIALIZING ADAPTER
    private SearchAdapter searchAdapter;

    //  INITIALIZE DATASET
    private List<User> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_follow, container, false);

        // CASTING VARIABLES of "fragment_follow.xml"
        followList_Name = view.findViewById(R.id.followList_Name);
        followList = view.findViewById(R.id.followList);
        followList.setHasFixedSize(true);
        followList.setLayoutManager(new LinearLayoutManager(getActivity()));

        //  BUNDLE COMING FROM CURRENT USER PROFILE TO DISPLAY FOLLOW INFO
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            command = bundle.getString("command");
            currentID = bundle.getString("currentID");
            userName = bundle.getString("userName");
        }

        displayFollowInfo(command, currentID);

        searchAdapter = new SearchAdapter(getContext(), mData);

        followList.setAdapter(searchAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mData = new ArrayList<>();

    }



    private void displayFollowInfo(String command, String currentID)
    {
        String order = "list_current_followers";
        String order2 = "list_current_following";
        String order3 = "list_searched_followers";
        String order4 = "list_searched_following";

        //  GIVE DATA TO ADAPTER "current user's followers"
        if(command.equals(order))
        {
            followList_Name.setText("Followers");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID).child("followers");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String targetID = dataSnapshot1.getKey();

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(targetID);
                        reference1.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {

                                if(dataSnapshot.exists())
                                {
                                    User user = dataSnapshot.getValue(User.class);
                                    mData.add(user);

                                    searchAdapter.notifyDataSetChanged();
                                }
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

            mData.clear();
        }

        //  GIVE DATA TO ADAPTER "current user's following"
        if(command.equals(order2))
        {
            followList_Name.setText("Following");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID).child("following");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String targetID = dataSnapshot1.getKey();

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(targetID);
                        reference1.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.exists())
                                {
                                    User user = dataSnapshot.getValue(User.class);
                                    mData.add(user);

                                    searchAdapter.notifyDataSetChanged();
                                }
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

            mData.clear();
        }

        //  GIVE DATA TO ADAPTER "searched's user followers"
        if(command.equals(order3))
        {
            followList_Name.setText("Followers Of " + userName);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID).child("followers");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String targetID = dataSnapshot1.getKey();

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(targetID);
                        reference1.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.exists())
                                {
                                    User user = dataSnapshot.getValue(User.class);
                                    mData.add(user);

                                    searchAdapter.notifyDataSetChanged();
                                }
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
            mData.clear();
        }

        //  GIVE DATA TO ADAPTER "searched's user following"
        if(command.equals(order4))
        {
            followList_Name.setText(userName +" Follows");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(currentID).child("following");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String targetID = dataSnapshot1.getKey();

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(targetID);
                        reference1.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.exists())
                                {
                                    User user = dataSnapshot.getValue(User.class);
                                    mData.add(user);

                                    searchAdapter.notifyDataSetChanged();
                                }
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
            mData.clear();
        }
    }
}
