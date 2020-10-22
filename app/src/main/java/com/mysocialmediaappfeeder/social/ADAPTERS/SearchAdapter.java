package com.mysocialmediaappfeeder.social.ADAPTERS;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.FRAGMENTS.SearchedFragment;
import com.mysocialmediaappfeeder.social.CLASSES.User;

import java.util.List;



public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>
{
    //  VARIABLES
    Context mContext;
    List<User> mData;
    User user;

    //  CONSTRUCTOR
    public SearchAdapter(Context mContext, List<User> mData)
    {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        view = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
        MyViewHolder vHolder = new MyViewHolder(view);


        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position)
    {
        user = mData.get(position);


        if(user != null)
        {
            holder.textView_Item_FullName.setText(user.getFullName());
            holder.textView_Item_UserName.setText(user.getUserName());
            Glide.with(mContext).load(user.getImageURL()).into(holder.Profile_CircleImage);

            //  Discovery fragment user click to view clicked user profile
            holder.layout_clickable.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    userClick(mData.get(position), v);
                }
            });
        }

        else
        {

        }
    }

    //  Used Bundle to pass data of clicked user. Opening that fragment was vital.
    public void userClick(User user, View v)
    {
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
    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView Profile_CircleImage;
        private TextView textView_Item_FullName;
        private TextView textView_Item_UserName;
        private LinearLayout layout_clickable;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            Profile_CircleImage = itemView.findViewById(R.id.Profile_CircleImage);
            textView_Item_FullName = itemView.findViewById(R.id.textView_Item_FullName);
            textView_Item_UserName = itemView.findViewById(R.id.textView_Item_UserName);
            layout_clickable = itemView.findViewById(R.id.layout_clickable);
        }
    }
}
