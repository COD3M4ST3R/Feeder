package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class UpdateFragment extends Fragment
{
      View view;

    //  XML
    TextView version;
    TextView updateDate;

    String v = "Version ";
    String n = "Next Planned Update: ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_update, container, false);

        //  XML
        version = view.findViewById(R.id.version);
        updateDate = view.findViewById(R.id.updateDate);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Support").child("Update");
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                version.setText((v + dataSnapshot.child("Version").getValue(String.class)));
                updateDate.setText((n + dataSnapshot.child("NextUpdate").getValue(String.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        return view;
    }
}
