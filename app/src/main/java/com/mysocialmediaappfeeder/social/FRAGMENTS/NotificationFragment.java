package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mysocialmediaappfeeder.social.ADAPTERS.NotificationAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.Notification;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationFragment extends Fragment
{
    View view;

    //  VARIABLES
    List<Notification> mData;

    //  XML VARIABLES
    RecyclerView rv_notifications;

    //  ADAPTER
    NotificationAdapter notificationAdapter;

    long currentTime = System.currentTimeMillis();
    long epox = currentTime - 864000000; // 10 DAY


    //  EMPTY CONSTRUCTOR
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);


        //  XML VARIABLES
        rv_notifications = view.findViewById(R.id.rv_notifications);

        mData.clear();

        //  ADAPTER SET
        rv_notifications.setHasFixedSize(true);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_notifications.setLayoutManager(linearLayoutManager);

        notificationAdapter = new NotificationAdapter(getContext(), mData);
        rv_notifications.setAdapter(notificationAdapter);


        receiveNotifications();




        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mData = new ArrayList<>();
    }

    private void receiveNotifications()
    {
        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(currentID);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Notification notification = dataSnapshot1.getValue(Notification.class);

                    if(notification.getDate() > epox)
                    {
                        mData.add(notification);
                    }
                }
                Collections.reverse(mData);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
