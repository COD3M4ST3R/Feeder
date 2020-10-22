package com.mysocialmediaappfeeder.social.ADAPTERS;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.CLASSES.Notification;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.FRAGMENTS.SearchedFragment;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>
{
    //  VARIABLES
    Context context;
    List<Notification> mData;

    //  DATE FORMAT
    String format = "yyyy-MM-dd / HH:mm:ss";
    final SimpleDateFormat sdf = new SimpleDateFormat(format);


    public NotificationAdapter(Context context, List<com.mysocialmediaappfeeder.social.CLASSES.Notification> mData)
    {
        this.context = context;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return  myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        holder.setIsRecyclable(false);


        if(mData.get(holder.getAdapterPosition()).getType().equals("FOLLOW"))
        {
            //  GET SENDER INFO
            final String senderID = mData.get(holder.getAdapterPosition()).getSenderID();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(senderID).exists())
                    {
                        User user = dataSnapshot.child(senderID).getValue(User.class);

                        Glide.with(context).load(user.getImageURL()).into(holder.userImage);
                        holder.userName.setText(user.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });

            //  GET NOTIFICATION INFO
            //holder.notificationMessage.setText(mData.get(holder.getAdapterPosition()).getMessage());
            holder.notificationDate.setText(sdf.format(new Date(mData.get(holder.getAdapterPosition()).getDate())));

            //  USER CLICK
            holder.userGo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    userClick(senderID, v);
                }
            });
        }




        if(mData.get(holder.getAdapterPosition()).getType().equals("LIKE TEXT"))
        {
            //  GET SENDER INFO
            final String senderID = mData.get(holder.getAdapterPosition()).getSenderID();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(senderID).exists())
                    {
                        User user = dataSnapshot.child(senderID).getValue(User.class);

                        Glide.with(context).load(user.getImageURL()).into(holder.userImage);
                        holder.userName.setText(user.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });

            //  GET NOTIFICATION INFO
            holder.notificationMessage.setText(mData.get(holder.getAdapterPosition()).getMessage());

        }

        if(mData.get(holder.getAdapterPosition()).getType().equals("LIKE IMAGE"))
        {
            //  GET SENDER INFO
            final String senderID = mData.get(holder.getAdapterPosition()).getSenderID();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(senderID).exists())
                    {
                        User user = dataSnapshot.child(senderID).getValue(User.class);

                        Glide.with(context).load(user.getImageURL()).into(holder.userImage);
                        holder.userName.setText(user.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });

            //  GET NOTIFICATION INFO
            holder.notificationMessage.setText(mData.get(holder.getAdapterPosition()).getMessage());
            Glide.with(context).load(mData.get(holder.getAdapterPosition()).getPostImage()).into(holder.notificationImage);

        }


        if(mData.get(holder.getAdapterPosition()).getType().equals("COMMENT TEXT"))
        {
            //  GET SENDER INFO
            final String senderID = mData.get(holder.getAdapterPosition()).getSenderID();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(senderID).exists())
                    {
                        User user = dataSnapshot.child(senderID).getValue(User.class);

                        Glide.with(context).load(user.getImageURL()).into(holder.userImage);
                        holder.userName.setText(user.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }

        if(mData.get(holder.getAdapterPosition()).getType().equals("COMMENT IMAGE"))
        {
            //  GET SENDER INFO
            final String senderID = mData.get(holder.getAdapterPosition()).getSenderID();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(senderID).exists())
                    {
                        User user = dataSnapshot.child(senderID).getValue(User.class);

                        Glide.with(context).load(user.getImageURL()).into(holder.userImage);
                        holder.userName.setText(user.getUserName());
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
    public int getItemCount()
    {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        //  XML VARIABLES OF "item_notification"
        CircleImageView userImage;
        TextView userName;
        TextView notificationMessage;
        ImageView notificationImage;
        TextView notificationDate;
        Button userGo;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            //  XML VARIABLES OF "item_notification"
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationImage = itemView.findViewById(R.id.notificationImage);
            notificationDate = itemView.findViewById(R.id.notificationDate);
            userGo = itemView.findViewById(R.id.userGo);
        }
    }

    private void userClick(String targetID, final View v)
    {
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
}
