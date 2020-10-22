package com.mysocialmediaappfeeder.social.ADAPTERS;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.CLASSES.Comments;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.FRAGMENTS.SearchedFragment;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;




public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>
{
    //  VARIABLES
    private List<Comments> mData;
    private Context context;
    private Comments comments;
    User user;
    User userc;


    public CommentAdapter(Context context, List<Comments> mData)
    {
        setHasStableIds(true);
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        holder.setIsRecyclable(false);

        // CHECK USER FOR DELETING COMMENT
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mData.get(holder.getAdapterPosition()).getCommentatorID()))
        {
            holder.comment_delete.setVisibility(View.VISIBLE);
        }

        else
        {
            holder.comment_delete.setVisibility(View.INVISIBLE);
        }

        comments = mData.get(holder.getAdapterPosition());
        final String targetID = comments.getCommentatorID();

        holder.comment_text.setText(comments.getComment());
        //  SET COMMENTATOR's PROFILE by CODING HAS RETURN VALUE FUNCTIONS WHICH HAS REFERENCE TO SEARCH USER REFERENCE AND GETTING USER CLASS AS A TARGET ID
        getCommentatorData(comments.getCommentatorID(), holder.comment_ProfileImage, holder.comment_FullName);


        //  DELETE COMMENT
        holder.comment_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setCancelable(true);
                builder.setTitle("Delete Comment?");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //  USER OWN THAT COMMENT, DELETE IT
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mData.get(holder.getAdapterPosition()).getCommentatorID()))
                        {
                            updateCommentCounter(mData.get(holder.getAdapterPosition()).getPostID());

                            Toast.makeText(context, "Comment Deleted.", Toast.LENGTH_SHORT).show();

                            final String deleteID = mData.get(holder.getAdapterPosition()).getCommentID();

                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments");
                            reference.child(deleteID).removeValue();
                        }

                        else
                        {
                            Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });


        //  WHEN USER CLICK COMMENT TO SEE COMMENTATOR's PROFILE
        holder.item_comment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(targetID);
                reference.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            userc = dataSnapshot.getValue(User.class);

                            //  CREATE A BUNDLE AND SEND IT TO "SearchFragment"
                            Bundle data = new Bundle();
                            data.putString("fullName", userc.getFullName());
                            data.putString("userName", userc.getUserName());
                            data.putString("bio", userc.getBio());
                            data.putString("imageURL", userc.getImageURL());
                            data.putString("authID", userc.getAuthID());
                            data.putString("accountType", user.getAccountType());

                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
                            SearchedFragment searchedFragment = new SearchedFragment();

                            searchedFragment.setArguments(data);

                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchedFragment).addToBackStack(null).commit();
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
        });
    }


    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        //  INITIALIZING XML VARIABLES OF "item_comment.xml"
        CircleImageView comment_ProfileImage;
        TextView comment_text;
        TextView comment_FullName;
        ConstraintLayout item_comment;
        ImageButton comment_delete;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            //  CASTING XML VARIABLES OF "item_comment.xml"
            comment_ProfileImage = itemView.findViewById(R.id.comment_ProfileImage);
            comment_text = itemView.findViewById(R.id.comment_text);
            comment_FullName = itemView.findViewById(R.id.comment_FullName);
            item_comment = itemView.findViewById(R.id.item_comment);
            comment_delete = itemView.findViewById(R.id.comment_delete);
        }
    }

    private void getCommentatorData(String commentatorID, final CircleImageView comment_ProfileImage, final TextView comment_FullName)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(commentatorID);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    user = dataSnapshot.getValue(User.class);
                    Glide.with(context).load(user.getImageURL()).into(comment_ProfileImage);
                    comment_FullName.setText(user.getFullName());
                }else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void updateCommentCounter(final String targetID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CommentCounter");
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child(targetID).exists())
                {
                    int counter = snapshot.child(targetID).getValue(int.class);
                    counter = counter - 1;
                    FirebaseDatabase.getInstance().getReference("CommentCounter").child(targetID).setValue(counter);
                }

                else
                {
                    Toast.makeText(context, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}
