package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SuggestionsFragment extends Fragment
{
    View view;

    //  XML
    EditText text;
    Button send;

    String t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_suggestions, container, false);


        //  XML
        text = view.findViewById(R.id.text);
        send = view.findViewById(R.id.send);

        //  SEND
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                t = text.getText().toString().toLowerCase();

                if(t.isEmpty())
                {
                    Toast.makeText(getContext(), "Please enter the suggestion.", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setCancelable(true);
                    builder.setTitle("Send?");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    builder.setPositiveButton("Send", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Support").child("Suggestions");

                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("suggestion", t);

                            reference.push().setValue(hashMap);

                            Toast.makeText(getContext(), "Thank you for your Feedback!", Toast.LENGTH_SHORT).show();

                            open_InfoFragment();
                        }
                    });
                    builder.show();
                }
            }
        });
        return view;
    }


    private void open_InfoFragment()
    {
        AppCompatActivity appCompatActivity = (AppCompatActivity)view.getContext();
        InfoFragment infoFragment = new InfoFragment();
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, infoFragment).addToBackStack(null).commit();
    }
}
