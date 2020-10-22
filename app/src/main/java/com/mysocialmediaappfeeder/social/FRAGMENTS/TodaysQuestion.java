package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mysocialmediaappfeeder.social.ACTIVITIES.MainActivity;
import com.mysocialmediaappfeeder.social.ACTIVITIES.PostActivity;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;

import java.util.HashMap;


public class TodaysQuestion extends Fragment
{
   View view;

   //   XML
   TextView question;
   EditText answer;
   Button send;
   ProgressBar indeterminateBar;

   String q;

   Long date;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_todays_question, container, false);


        //  XML
        question = view.findViewById(R.id.question);
        answer = view.findViewById(R.id.answer);
        send = view.findViewById(R.id.send);
        indeterminateBar = view.findViewById(R.id.indeterminateBar);

        //  SET VISIBILITY
        indeterminateBar.setVisibility(View.GONE);


        //  PLACE THE QUESTION
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Question").child("Question");
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    question.setText(snapshot.getValue(String.class));
                    q = question.getText().toString();
                }

                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


        //  SEND THE ANSWER
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(answer.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(), "Enter Your Answer First!", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    indeterminateBar.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);

                    date = System.currentTimeMillis();

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference1.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.exists())
                            {
                                User user = snapshot.getValue(User.class);

                                //  USER POSTING TEXTFEED
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                                String postID = reference.push().getKey();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("postID", postID);
                                hashMap.put("postOwner", user.getAuthID());
                                hashMap.put("postType", "textFeed");
                                hashMap.put("postImage", "false");
                                hashMap.put("description", q + user.getUserName() + " answered as: " + answer.getText().toString());
                                hashMap.put("date", date);

                                reference.child(postID).setValue(hashMap);

                                countPost();

                                indeterminateBar.setVisibility(View.INVISIBLE);

                                Toast.makeText(getContext(), "Your Answer Send!", Toast.LENGTH_SHORT).show();

                                open_ProfileFragment(v);
                            }

                            else
                            {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
                }
            }
        });

        return view;
    }

    private void open_ProfileFragment(View v)
    {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        ProfileFragment profileFragment = new ProfileFragment();

        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).addToBackStack(null).commit();
    }

    private void countPost()
    {
        //  UPDATE FEED COUNTER

        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("PostCounter");
        reference2.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //  USER POSTED BEFORE; GET THE VALUE AND UPDATE IT WITh +1
                if(dataSnapshot.child(currentID).exists())
                {
                    int counter = dataSnapshot.child(currentID).getValue(int.class);
                    counter = counter + 1;
                    FirebaseDatabase.getInstance().getReference().child("PostCounter").child(currentID).setValue(counter);
                }

                //  IT IS USER's FIRST POST
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("PostCounter").child(currentID).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
