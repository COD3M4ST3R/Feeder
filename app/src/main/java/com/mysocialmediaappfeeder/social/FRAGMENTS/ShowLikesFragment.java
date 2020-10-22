package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mysocialmediaappfeeder.social.ADAPTERS.CommentAdapter;
import com.mysocialmediaappfeeder.social.ADAPTERS.ShowLikesAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;

import java.util.ArrayList;
import java.util.List;

public class ShowLikesFragment extends Fragment
{
    View view;

    //  DATASET
    List<String> mData;

    //  VARIABLE
    String postID;
    User user;

    //  ADAPTER
    ShowLikesAdapter showLikesAdapter;

    //  XML
    RecyclerView rv_likes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_show_likes, container, false);

        //  CLEAR DATASET TO PREVENT CLONING OF DATA BUG
        mData.clear();

        //  XML
        rv_likes= view.findViewById(R.id.rv_likes);

        //  SET ADAPTER
        rv_likes.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_likes.setLayoutManager(linearLayoutManager);
        showLikesAdapter = new ShowLikesAdapter(getContext(), mData);
        rv_likes.setAdapter(showLikesAdapter);

        //  GETTING BUNDLE FROM POST ADAPTER
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            postID = bundle.getString("postID");
        }

        retrieveData(postID);


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mData = new ArrayList<>();

    }

    private void retrieveData(final String targetID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(targetID);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        String key = snapshot1.getKey();
                        mData.add(key);
                    }
                    showLikesAdapter.notifyDataSetChanged();
                }

                else
                {
                    //  No Likes To Show
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}
