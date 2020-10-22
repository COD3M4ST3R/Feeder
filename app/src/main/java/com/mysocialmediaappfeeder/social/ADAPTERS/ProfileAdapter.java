package com.mysocialmediaappfeeder.social.ADAPTERS;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.CLASSES.Posts;
import com.mysocialmediaappfeeder.social.FRAGMENTS.CommentFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.ShowLikesFragment;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class ProfileAdapter extends RecyclerView.Adapter
{
    //  VARIABLES
    List<Posts> mData;
    Context context;
    Posts posts;

    //  VIEWHOLDERS
    private ViewHolderOne viewHolderOne;
    private ViewHolderTwo viewHolderTwo;


    //  FORMATTING MILISECOND POST TIME TO (SIMP???)LE DISPLAY
    String format = "yyyy-MM-dd / HH:mm:ss";
    final SimpleDateFormat sdf = new SimpleDateFormat(format);

    public ProfileAdapter(Context context, List<Posts> mData)
    {
        this.context = context;
        this.mData = mData;
        //setHasStableIds(true);
    }


    //  DECISION MAKER BETWEEN POST TYPES (textFEED or imageFEED)?
    @Override
    public int getItemViewType(int position)
    {
        posts = mData.get(position);

        if(posts.getPostType().equals("imageFeed"))
        {
            return 1;   //  IMAGEFEED
        }

        else
        {
            return 0;   //  TEXTFEED
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if(viewType == 1)
        {
            view = LayoutInflater.from(context).inflate(R.layout.profile_imageitem, parent, false);

            return  new ProfileAdapter.ViewHolderTwo(view);
        }

        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.profile_textitem, parent, false);

            return new ProfileAdapter.ViewHolderOne(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position)
    {

        holder.setIsRecyclable(false);


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  IMAGEFEED
        if(getItemViewType(holder.getAdapterPosition()) == 1)
        {
            viewHolderTwo = (ProfileAdapter.ViewHolderTwo) holder;

            //  CASTING XML VARIABLES
            Glide.with(context).load(mData.get(holder.getAdapterPosition()).getPostImage()).into(viewHolderTwo.profile_image);
            viewHolderTwo.description.setText(mData.get(holder.getAdapterPosition()).getDescription());
            viewHolderTwo.ImageFeed_Date.setText(sdf.format(new Date(mData.get(holder.getAdapterPosition()).getDate())));
            likeCounter(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeNumber);
            changeButton(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeButtonRED);
            commentCounter(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_CommentNumber);

            // CHECK USER FOR DELETING POST
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mData.get(holder.getAdapterPosition()).getPostOwner()))
            {
                viewHolderTwo.ImageFeed_Delete.setVisibility(View.VISIBLE);
            }

            else
            {
                viewHolderTwo.ImageFeed_Delete.setVisibility(View.INVISIBLE);
            }


            //  LIKE BUTTON CLICK LISTENER
            viewHolderTwo.ImageFeed_LikeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    userLike(mData.get(holder.getAdapterPosition()));
                    likeCounter(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeNumber);
                    changeButton(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeButtonRED);
                }
            });

            //  COMMENT BUTTON
            viewHolderTwo.ImageFeed_Comments.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle data = new Bundle();
                    data.putString("postID", mData.get(holder.getAdapterPosition()).getPostID());

                    //  OPEN COMMENT FRAGMENT
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    CommentFragment commentFragment = new CommentFragment();

                    commentFragment.setArguments(data);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).addToBackStack(null).commit();
                }
            });

            //  SHOW LIKES
            viewHolderTwo.ImageFeed_LikeNumber.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle data = new Bundle();
                    data.putString("postID", mData.get(holder.getAdapterPosition()).getPostID());

                    //  OPEN COMMENT FRAGMENT
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    ShowLikesFragment showLikesFragment = new ShowLikesFragment();

                    showLikesFragment.setArguments(data);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, showLikesFragment).addToBackStack(null).commit();
                }
            });

            //  DELETE POST
            viewHolderTwo.ImageFeed_Delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setCancelable(true);
                    builder.setTitle("Delete Post?");

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
                            //  USER OWN THAT POST, DELETE IT
                            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mData.get(holder.getAdapterPosition()).getPostOwner()))
                            {
                                Toast.makeText(context, "Post Deleted.", Toast.LENGTH_SHORT).show();

                                final String deleteID = mData.get(holder.getAdapterPosition()).getPostID();

                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                                reference.child(deleteID).removeValue();

                                final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                //  UPDATE THE POST COUNTER
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("PostCounter").child(currentID);
                                reference1.addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(snapshot.exists())
                                        {
                                            int number = snapshot.getValue(int.class);
                                            number = number - 1;

                                            FirebaseDatabase.getInstance().getReference("PostCounter").child(currentID).setValue(number);
                                        }

                                        else
                                        {
                                            //  There are no any post number to update
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {

                                    }
                                });
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
        }








        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   TEXTFEED
        if(getItemViewType(holder.getAdapterPosition()) == 0)
        {
            viewHolderOne = (ProfileAdapter.ViewHolderOne) holder;

            //  CASTING XML VARIABLES
            viewHolderOne.profile_text.setText(mData.get(holder.getAdapterPosition()).getDescription());
            viewHolderOne.TextFeed_Date.setText(sdf.format(new Date(mData.get(holder.getAdapterPosition()).getDate())));
            likeCounter(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeNumber);
            changeButton(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeButtonRED);
            commentCounter(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_CommentNumber);


            // CHECK USER FOR DELETING POST
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mData.get(holder.getAdapterPosition()).getPostOwner()))
            {
                viewHolderOne.TextFeed_Delete.setVisibility(View.VISIBLE);
            }

            else
            {
                viewHolderOne.TextFeed_Delete.setVisibility(View.INVISIBLE);
            }

            // LIKE BUTTON CLICK LISTENER
            viewHolderOne.TextFeed_LikeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    userLike(mData.get(holder.getAdapterPosition()));
                    likeCounter(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeNumber);
                    changeButton(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeButtonRED);
                }
            });

            //COMMENT BUTTON
            viewHolderOne.TextFeed_Comments.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle data = new Bundle();
                    data.putString("postID", mData.get(holder.getAdapterPosition()).getPostID());

                    //  OPEN COMMENT FRAGMENT
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    CommentFragment commentFragment = new CommentFragment();

                    commentFragment.setArguments(data);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).addToBackStack(null).commit();
                }
            });

            //  SHOW LIKES
            viewHolderOne.TextFeed_LikeNumber.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle data = new Bundle();
                    data.putString("postID", mData.get(holder.getAdapterPosition()).getPostID());

                    //  OPEN COMMENT FRAGMENT
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    ShowLikesFragment showLikesFragment = new ShowLikesFragment();

                    showLikesFragment.setArguments(data);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, showLikesFragment).addToBackStack(null).commit();
                }
            });

            //  DELETE POST
            viewHolderOne.TextFeed_Delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setCancelable(true);
                    builder.setTitle("Delete Post?");

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
                            //  USER OWN THAT POST, DELETE IT
                            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mData.get(holder.getAdapterPosition()).getPostOwner()))
                            {
                                Toast.makeText(context, "Post Deleted.", Toast.LENGTH_SHORT).show();

                                final String deleteID = mData.get(holder.getAdapterPosition()).getPostID();

                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                                reference.child(deleteID).removeValue();

                                final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                //  UPDATE THE POST COUNTER
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("PostCounter").child(currentID);
                                reference1.addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(snapshot.exists())
                                        {
                                            int number = snapshot.getValue(int.class);
                                            number = number - 1;

                                            FirebaseDatabase.getInstance().getReference("PostCounter").child(currentID).setValue(number);
                                        }

                                        else
                                        {
                                            //  There are no any post number to update
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {

                                    }
                                });

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

        }
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    //  VIEWHOLDER FOR TEXT CONTENT
    private class ViewHolderOne extends RecyclerView.ViewHolder
    {
        ConstraintLayout profile_textitem;
        TextView profile_text;
        TextView TextFeed_Date;
        TextView TextFeed_LikeNumber;
        ImageButton TextFeed_LikeButton;
        ImageView TextFeed_LikeButtonRED;
        ImageButton TextFeed_Comments;
        ImageButton TextFeed_Delete;
        TextView TextFeed_CommentNumber;

        public ViewHolderOne(@NonNull View itemView)
        {
            super(itemView);

            profile_textitem = itemView.findViewById(R.id.profile_textitem);
            profile_text = itemView.findViewById(R.id.profile_text);
            TextFeed_Date = itemView.findViewById(R.id.TextFeed_Date);
            TextFeed_LikeNumber = itemView.findViewById(R.id.TextFeed_LikeNumber);
            TextFeed_LikeButton = itemView.findViewById(R.id.TextFeed_LikeButton);
            TextFeed_LikeButtonRED = itemView.findViewById(R.id.TextFeed_LikeButtonRED);
            TextFeed_Comments = itemView.findViewById(R.id.TextFeed_Comments);
            TextFeed_Delete = itemView.findViewById(R.id.TextFeed_Delete);
            TextFeed_CommentNumber = itemView.findViewById(R.id.TextFeed_CommentNumber);
        }
    }

    //  VIEWHOLDER FOR IMAGE CONTENT
    private class ViewHolderTwo extends  RecyclerView.ViewHolder
    {
        ConstraintLayout profile_imageitem;
        ImageView profile_image;
        TextView description;
        TextView ImageFeed_Date;
        TextView ImageFeed_LikeNumber;
        ImageButton ImageFeed_LikeButton;
        ImageView ImageFeed_LikeButtonRED;
        ImageButton ImageFeed_Comments;
        ImageButton ImageFeed_Delete;
        TextView ImageFeed_CommentNumber;

        public ViewHolderTwo(@NonNull View itemView)
        {
            super(itemView);

            profile_imageitem = itemView.findViewById(R.id.profile_imageitem);
            profile_image = itemView.findViewById(R.id.profile_image);
            description = itemView.findViewById(R.id.description);
            ImageFeed_Date = itemView.findViewById(R.id.ImageFeed_Date);
            ImageFeed_LikeNumber = itemView.findViewById(R.id.ImageFeed_LikeNumber);
            ImageFeed_LikeButton = itemView.findViewById(R.id.ImageFeed_LikeButton);
            ImageFeed_LikeButtonRED = itemView.findViewById(R.id.ImageFeed_LikeButtonRED);
            ImageFeed_Comments = itemView.findViewById(R.id.ImageFeed_Comments);
            ImageFeed_Delete = itemView.findViewById(R.id.ImageFeed_Delete);
            ImageFeed_CommentNumber = itemView.findViewById(R.id.ImageFeed_CommentNumber);
        }
    }

    //  METHOD's
    private void likeCounter(Posts posts, final TextView likeCounter)
    {
        final String targetID = posts.getPostID();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LikeCounter");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(targetID).exists())
                {
                    likeCounter.setText(String.valueOf(dataSnapshot.child(targetID).getValue(int.class)));
                }

                else
                {
                    likeCounter.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void commentCounter(Posts posts, final TextView commentCounter)
    {
        final  String targetID = posts.getPostID();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CommentCounter");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child(targetID).exists())
                {
                    commentCounter.setText(String.valueOf(snapshot.child(targetID).getValue(int.class)));
                }

                else
                    commentCounter.setText(String.valueOf(0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void userLike(Posts posts)
    {
        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String targetID = posts.getPostID();

        //  CHECK IF USER ALREADY LIKED THAT TARGETPOST
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //  THIS POST HAS ATLEAST 1 LIKE; CHECK BOTH CONDITIONS
                if (dataSnapshot.child(targetID).exists()) {
                    //  USER CAN EITHER DISLIKE IT OR LIKE IT
                    //  CHECK IF currentID ALREADY LIKED THE POST
                    if (dataSnapshot.child(targetID).child(currentID).exists()) {
                        //  USER WANT TO DISLIKE IT
                        FirebaseDatabase.getInstance().getReference("Likes").child(targetID).child(currentID).removeValue();
                        //  UPDATE THE POST LIKES
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("LikeCounter");
                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int like = dataSnapshot.child(targetID).getValue(int.class);
                                like = like - 1;
                                FirebaseDatabase.getInstance().getReference("LikeCounter").child(targetID).setValue(like);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        //  USER WANT TO LIKE
                        FirebaseDatabase.getInstance().getReference("Likes").child(targetID).child(currentID).setValue(true);
                        //  UPDATE THE POST LIKES
                        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("LikeCounter");
                        reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int like = dataSnapshot.child(targetID).getValue(int.class);
                                like = like + 1;
                                FirebaseDatabase.getInstance().getReference("LikeCounter").child(targetID).setValue(like);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                //  CURRENT WILL BE THE FIRST ONE TO LIKE THIS POST
                else {
                    FirebaseDatabase.getInstance().getReference("Likes").child(targetID).child(currentID).setValue(true);
                    //  UPDATE THE POST LIKES
                    FirebaseDatabase.getInstance().getReference("LikeCounter").child(targetID).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeButton(Posts posts, final ImageView visualRED)
    {
        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String targetID = posts.getPostID();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(targetID).exists())
                {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Likes").child(targetID);
                    reference1.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.hasChild(currentID))
                            {
                                visualRED.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                visualRED.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                    });
                }

                else
                {
                    visualRED.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
