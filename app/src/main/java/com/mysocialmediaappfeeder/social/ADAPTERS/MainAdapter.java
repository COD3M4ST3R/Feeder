package com.mysocialmediaappfeeder.social.ADAPTERS;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.CLASSES.Posts;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.FRAGMENTS.CommentFragment;
import com.mysocialmediaappfeeder.social.FRAGMENTS.SearchedFragment;
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
import java.util.HashMap;
import java.util.List;



public class MainAdapter extends RecyclerView.Adapter
{
    //  VARIABLES
    private List<Posts> mData;
    private Context context;
    private Posts posts;
    int like;

    //  TRANSFER POSITION OF ADAPTER TO FeedFragment
    private MainAdapterInterface mainAdapterInterface;


    //   VIEWHOLDERS
    private ViewHolderTwo viewHolderTwo;
    private ViewHolderOne viewHolderOne;

    //  FORMATTING MILISECOND POST TIME TO (SIMP???)LE DISPLAY
    String format = "yyyy-MM-dd / HH:mm:ss";
    final SimpleDateFormat sdf = new SimpleDateFormat(format);

    public MainAdapter(Context context, List<Posts> mData, MainAdapterInterface mainAdapterInterface)
    {
        setHasStableIds(true);
        this.context = context;
        this.mData = mData;
        this.mainAdapterInterface = mainAdapterInterface;
    }


    //  DECISION MAKER

    @Override
    public int getItemViewType(int position)
    {
        if(mData.get(position) == null)
        {
            return 3;
        }

        else
        {
            posts = mData.get(position);

            if(posts.getPostType().equals("imageFeed"))
            {
                return 1;
            }

            else
            {
                return 0;
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;


        if (viewType == 1)
        {
            view = LayoutInflater.from(context).inflate(R.layout.imagefeed_item, parent, false);

            return new MainAdapter.ViewHolderTwo(view);
        }

        view = LayoutInflater.from(context).inflate(R.layout.textfeed_item, parent, false);
        return new MainAdapter.ViewHolderOne(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position)
    {
        //  RECYCLE
        holder.setIsRecyclable(false);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   IMAGE FEED
        if(getItemViewType(holder.getAdapterPosition()) == 1)
        {
            //  CAST HOLDER TO CUSTOM HOLDER
            viewHolderTwo = (MainAdapter.ViewHolderTwo) holder;

            //  GET POST OWNER
            final String postOwnerID = mData.get(holder.getAdapterPosition()).getPostOwner();

            //  DATABASE SEARCH FOR POST OWNER INFORMATION
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.hasChild(postOwnerID))
                    {
                        User user = dataSnapshot.child(postOwnerID).getValue(User.class);

                        Glide.with(context).load(user.getImageURL()).into(viewHolderTwo.ImageFeed_UserImage);
                        viewHolderTwo.ImageFeed_FullName.setText(user.getFullName());
                        viewHolderTwo.ImageFeed_UserName.setText(user.getUserName());
                        Glide.with(context).load(mData.get(holder.getAdapterPosition()).getPostImage()).into(viewHolderTwo.ImageFeed_Feed);
                        viewHolderTwo.ImageFeed_FeedDescription.setText(mData.get(holder.getAdapterPosition()).getDescription());
                        viewHolderTwo.ImageFeed_Date.setText(sdf.format(new Date(mData.get(holder.getAdapterPosition()).getDate())));
                        likeCounter(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeNumber);
                        changeButton(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeButtonRED);
                        commentCounter(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_CommentNumber);


                        //  CHECK FEED BUTTON
                        feedCheck(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_FeedButton, viewHolderTwo.ImageFeed_Report);

                        //  USER CLICK
                        viewHolderTwo.ImageFeed_Layout.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                userClick(postOwnerID, v);
                            }
                        });

                        //  LIKE
                        viewHolderTwo.ImageFeed_LikeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userLike(mData.get(holder.getAdapterPosition()));
                                // SENDS POSITION OF ITEM TO FeedFragment
                                mainAdapterInterface.OnItemClicked(holder.getAdapterPosition(), 2);
                                likeCounter(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeNumber);
                                changeButton(mData.get(holder.getAdapterPosition()), viewHolderTwo.ImageFeed_LikeButtonRED);
                            }
                        });


                        //  COMMENTS
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


                        //  REPORT
                        viewHolderTwo.ImageFeed_Report.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setCancelable(true);
                                builder.setTitle("Report Post?");

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel();
                                    }
                                });

                                builder.setPositiveButton("Report", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Toast.makeText(context, "Post Reported.", Toast.LENGTH_SHORT).show();

                                        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        final String targetID = mData.get(holder.getAdapterPosition()).getPostID();
                                        final String targetOwnerID = mData.get(holder.getAdapterPosition()).getPostOwner();

                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports");
                                        reference.addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("reporter", currentID);
                                                hashMap.put("reportedPost", targetID);
                                                hashMap.put("reportedPostOwner", targetOwnerID);

                                                reference.child(targetID).setValue(hashMap);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                            {

                                            }
                                        });
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    else
                    {
                        //  User does not exist but have post on database exception
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    TEXT FEED
        if(getItemViewType(holder.getAdapterPosition()) == 0)
        {
            //  CAST HOLDER TO CUSTOM HOLDER
            viewHolderOne = (MainAdapter.ViewHolderOne) holder;

            //  GET POST OWNER
            final String postOwnerID = mData.get(holder.getAdapterPosition()).getPostOwner();

            //  DATABASE SEARCH FOR POST OWNER INFORMATION
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.hasChild(postOwnerID))
                    {
                        User user = dataSnapshot.child(postOwnerID).getValue(User.class);

                        Glide.with(context).load(user.getImageURL()).into(viewHolderOne.TextFeed_UserImage);
                        viewHolderOne.TextFeed_FullName.setText(user.getFullName());
                        viewHolderOne.TextFeed_UserName.setText(user.getUserName());
                        viewHolderOne.TextFeed_Text.setText(mData.get(holder.getAdapterPosition()).getDescription());
                        viewHolderOne.TextFeed_Date.setText(sdf.format(new Date(mData.get(holder.getAdapterPosition()).getDate())));
                        likeCounter(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeNumber);
                        changeButton(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeButtonRED);
                        commentCounter(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_CommentNumber);



                        //  CHECK FEED BUTTON
                        feedCheck(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_FeedButton, viewHolderOne.TextFeed_Report);

                        //  USER CLICK
                        viewHolderOne.TextFeed_Layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userClick(postOwnerID, v);
                            }
                        });

                        //  LIKE
                        viewHolderOne.TextFeed_LikeButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                userLike(mData.get(holder.getAdapterPosition()));
                                // SENDS POSITION OF ITEM TO FeedFragment
                                mainAdapterInterface.OnItemClicked(holder.getAdapterPosition(), 1);
                                likeCounter(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeNumber);
                                changeButton(mData.get(holder.getAdapterPosition()), viewHolderOne.TextFeed_LikeButtonRED);
                            }
                        });

                        //  COMMENT
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

                        //  REPORT
                        viewHolderOne.TextFeed_Report.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setCancelable(true);
                                builder.setTitle("Report Post?");

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel();
                                    }
                                });

                                builder.setPositiveButton("Report", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Toast.makeText(context, "Post Reported.", Toast.LENGTH_SHORT).show();

                                        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        final String targetID = mData.get(holder.getAdapterPosition()).getPostID();
                                        final String targetOwnerID = mData.get(holder.getAdapterPosition()).getPostOwner();

                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports");
                                        reference.addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("reporter", currentID);
                                                hashMap.put("reportedPost", targetID);
                                                hashMap.put("reportedPostOwner", targetOwnerID);

                                                reference.child(targetID).setValue(hashMap);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                            {

                                            }
                                        });
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
    }

    //  ITEM COUNT
    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    //  VIEW HOLDER FOR TEXT CONTENT
    private class ViewHolderOne extends RecyclerView.ViewHolder
    {
        //  INITIALIZING XML VARIABLES OF "textfeed_item.xml"
        private ImageView TextFeed_UserImage;
        private TextView TextFeed_FullName;
        private TextView TextFeed_UserName;
        private ImageButton TextFeed_FeedButton;
        private TextView TextFeed_Text;
        private ImageButton TextFeed_LikeButton;
        private ImageView TextFeed_LikeButtonRED;
        private TextView TextFeed_LikeNumber;
        private ImageButton TextFeed_Comments;
        private TextView TextFeed_Date;
        private LinearLayout TextFeed_Layout;
        private ImageButton TextFeed_Report;
        TextView TextFeed_CommentNumber;

        public ViewHolderOne(@NonNull View itemView)
        {
            super(itemView);

            //  CASTING XML VARIABLES OF "textfeed_item.xml"
            TextFeed_UserImage = itemView.findViewById(R.id.TextFeed_UserImage);
            TextFeed_FullName = itemView.findViewById(R.id.TextFeed_FullName);
            TextFeed_UserName = itemView.findViewById(R.id.TextFeed_UserName);
            TextFeed_FeedButton = itemView.findViewById(R.id.TextFeed_FeedButton);
            TextFeed_Text = itemView.findViewById(R.id.TextFeed_Text);
            TextFeed_LikeButton = itemView.findViewById(R.id.TextFeed_LikeButton);
            TextFeed_LikeButtonRED = itemView.findViewById(R.id.TextFeed_LikeButtonRED);
            TextFeed_LikeNumber = itemView.findViewById(R.id.TextFeed_LikeNumber);
            TextFeed_Comments = itemView.findViewById(R.id.TextFeed_Comments);
            TextFeed_Date = itemView.findViewById(R.id.TextFeed_Date);
            TextFeed_Layout = itemView.findViewById(R.id.TextFeed_Layout);
            TextFeed_Comments = itemView.findViewById(R.id.TextFeed_Comments);
            TextFeed_Report = itemView.findViewById(R.id.TextFeed_Report);
            TextFeed_CommentNumber = itemView.findViewById(R.id.TextFeed_CommentNumber);
        }
    }

    //  VIEW HOLDER FOR IMAGE CONTENT
    private class ViewHolderTwo extends  RecyclerView.ViewHolder
    {
        //  INITIALIZING XML VARIABLES OF "imagefeed_item.xml"
        private ImageView ImageFeed_UserImage;
        private TextView ImageFeed_FullName;
        private TextView ImageFeed_UserName;
        private ImageButton ImageFeed_FeedButton;
        private ImageView ImageFeed_Feed;
        private TextView ImageFeed_FeedDescription;
        private ImageButton ImageFeed_LikeButton;
        private ImageView ImageFeed_LikeButtonRED;
        private TextView ImageFeed_LikeNumber;
        private ImageButton ImageFeed_Comments;
        private TextView ImageFeed_Date;
        private LinearLayout ImageFeed_Layout;
        private ImageButton ImageFeed_Report;
        TextView ImageFeed_CommentNumber;

        public ViewHolderTwo(@NonNull View itemView)
        {
            super(itemView);

            //  INITIALIZING XML VARIABLES OF "imagefeed_item.xml"
            ImageFeed_UserImage = itemView.findViewById(R.id.ImageFeed_UserImage);
            ImageFeed_FullName = itemView.findViewById(R.id.ImageFeed_FullName);
            ImageFeed_UserName = itemView.findViewById(R.id.ImageFeed_UserName);
            ImageFeed_FeedButton = itemView.findViewById(R.id.ImageFeed_FeedButton);
            ImageFeed_Feed = itemView.findViewById(R.id.ImageFeed_Feed);
            ImageFeed_FeedDescription = itemView.findViewById(R.id.ImageFeed_FeedDescription);
            ImageFeed_LikeButton = itemView.findViewById(R.id.ImageFeed_LikeButton);
            ImageFeed_LikeButtonRED = itemView.findViewById(R.id.ImageFeed_LikeButtonRED);
            ImageFeed_LikeNumber = itemView.findViewById(R.id.ImageFeed_LikeNumber);
            ImageFeed_Comments = itemView.findViewById(R.id.ImageFeed_Comments);
            ImageFeed_Date = itemView.findViewById(R.id.ImageFeed_Date);
            ImageFeed_Layout = itemView.findViewById(R.id.ImageFeed_Layout);
            ImageFeed_Comments = itemView.findViewById(R.id.ImageFeed_Comments);
            ImageFeed_Report = itemView.findViewById(R.id.ImageFeed_Report);
            ImageFeed_CommentNumber = itemView.findViewById(R.id.ImageFeed_CommentNumber);
        }
    }


    //  METHODs
    private void userClick(String targetID, final View v) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(targetID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Bundle data = new Bundle();
                data.putString("fullName", user.getFullName());
                data.putString("userName", user.getUserName());
                data.putString("bio", user.getBio());
                data.putString("imageURL", user.getImageURL());
                data.putString("authID", user.getAuthID());
                data.putString("accountType", user.getAccountType());

                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                SearchedFragment searchedFragment = new SearchedFragment();

                searchedFragment.setArguments(data);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchedFragment).addToBackStack(null).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userLike(final Posts posts)
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
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.child(targetID).exists())
                                {
                                    like = dataSnapshot.child(targetID).getValue(int.class);

                                    if(like != 0)
                                    {
                                        like = like - 1;
                                        FirebaseDatabase.getInstance().getReference("LikeCounter").child(targetID).setValue(like);
                                    }

                                    else
                                    {
                                        like = 0;
                                    }
                                }

                                else
                                {
                                    like = 0;
                                }
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

    private void feedCheck(Posts posts, final ImageButton feedButton, ImageButton reportButton)
    {
        final String targetID = posts.getPostOwner();
        String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(currentID).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(targetID).exists())
                {
                    if(dataSnapshot.child(targetID).getValue(String.class).equals("feed"))
                    {
                        feedButton.setVisibility(View.VISIBLE);
                    }

                    else
                    {
                        feedButton.setVisibility(View.INVISIBLE);
                    }
                }

                else
                {
                    feedButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        if(posts.getPostOwner().equals(currentID))
        {
            reportButton.setVisibility(View.INVISIBLE);
        }
    }

    public interface MainAdapterInterface
    {
        void OnItemClicked(int pos, int flag);
    }

    //  TODO
    private void notificationLike(Posts posts)
    {
        //  targetID need to get notification
        final String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String targetID = posts.getPostOwner();
        long date = System.currentTimeMillis();
        String postID = posts.getPostID();

        String message = "liked your post";
        String type;

        if(posts.getPostType().equals("imageFeed"))
        {
            type = "LIKE IMAGE";
        }

        else
        {
            type = "LIKE TEXT";
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(targetID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderID", currentID);
        hashMap.put("receiverID", targetID);
        hashMap.put("message", message);
        hashMap.put("date", date);
        hashMap.put("type", type);
        hashMap.put("postID", postID);

        if(posts.getPostType().equals("imageFeed"))
        {
            hashMap.put("postImage", posts.getPostImage());
        }

        reference.push().setValue(hashMap);
    }
}
























