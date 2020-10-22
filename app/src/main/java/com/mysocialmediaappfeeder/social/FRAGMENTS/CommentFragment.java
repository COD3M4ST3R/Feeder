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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.ADAPTERS.CommentAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.Comments;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class CommentFragment extends Fragment
{

    View view;
    private String postID;
    private String currentID;
    private String text;

    //  INITIALIZING XML VARIABLES
    private EditText comment;
    private CircleImageView profile_image;
    private ImageButton comment_send;
    private RecyclerView showComments;

    //  ADAPTER
    CommentAdapter commentAdapter;

    //  DATASET
    private List<Comments> mData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_comment, container, false);

        //  CLEAR DATASET TO PREVENT CLONING OF DATA BUG
        mData.clear();

        // CASTING XML VARIABLES
        comment = view.findViewById(R.id.comment);
        profile_image = view.findViewById(R.id.profile_image);
        comment_send = view.findViewById(R.id.comment_send);
        showComments = view.findViewById(R.id.showComments);

        //  SET ADAPTER
        showComments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        showComments.setLayoutManager(linearLayoutManager);
        commentAdapter = new CommentAdapter(getContext(), mData);
        showComments.setAdapter(commentAdapter);

        //  GETTING BUNDLE FROM POST ADAPTER
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            postID = bundle.getString("postID");
        }

        currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        displayProfileImage(currentID);

        showComments(postID);

        comment_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //  GETTING COMMENT STRING
                text = comment.getText().toString();
                if(text.length() < 1)
                {
                    Toast.makeText(getContext(), "Please enter valid comment!", Toast.LENGTH_SHORT).show();
                }else
                {
                    sendComment(text, currentID, postID);
                    setCommentCounter(postID);
                    comment.setText("");
                }
            }
        });



        return  view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
    }

    private void displayProfileImage(String currentID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentID);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void sendComment(String text, String currentID, String postID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments");

        String commentID = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("commentID", commentID);
        hashMap.put("commentatorID", currentID);
        hashMap.put("postID", postID);
        hashMap.put("comment", text);

        reference.child(commentID).setValue(hashMap);
    }

    private void showComments(String postID)
    {
        Query query = FirebaseDatabase.getInstance().getReference("Comments").orderByChild("postID").equalTo(postID);
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Comments comments = dataSnapshot1.getValue(Comments.class);
                    mData.add(comments);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void setCommentCounter(final String targetID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CommentCounter");
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child(targetID).exists())
                {
                    // Get and Update int data
                    int counter = snapshot.child(targetID).getValue(int.class);
                    counter = counter + 1;
                    FirebaseDatabase.getInstance().getReference("CommentCounter").child(targetID).setValue(counter);
                }

                else
                {
                    // Create data as 1
                    FirebaseDatabase.getInstance().getReference("CommentCounter").child(targetID).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}

