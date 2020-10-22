package com.mysocialmediaappfeeder.social.ADAPTERS;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.FRAGMENTS.SearchedFragment;
import com.mysocialmediaappfeeder.social.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;



public class ShowLikesAdapter  extends RecyclerView.Adapter<ShowLikesAdapter.MyViewHolder>
{
    //  VARIABLES
    List<String> mData;
    Context context;

    public ShowLikesAdapter(Context context, List<String>mData)
    {
        setHasStableIds(true);
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.item_showlikes, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        holder.setIsRecyclable(false);

        final String key = mData.get(holder.getAdapterPosition());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(key);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);

                    Glide.with(context).load(user.getImageURL()).into(holder.Profile_CircleImage);
                    holder.FullName.setText(user.getFullName());
                    holder.UserName.setText(user.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


        //  USER CLICK
        holder.showlikes_layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(key);
                reference.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);

                            //  CREATE A BUNDLE AND SEND IT TO "SearchFragment"
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
        //  XML Variables of "search_item"
        CircleImageView Profile_CircleImage;
        TextView FullName;
        TextView UserName;
        LinearLayout showlikes_layout;


        public MyViewHolder(View itemView)
        {
            super(itemView);

            //  XML Variables of "search_item"
            Profile_CircleImage = itemView.findViewById(R.id.Profile_CircleImage);
            FullName = itemView.findViewById(R.id.FullName);
            UserName = itemView.findViewById(R.id.UserName);
            showlikes_layout = itemView.findViewById(R.id.showlikes_layout);
        }
    }
}
