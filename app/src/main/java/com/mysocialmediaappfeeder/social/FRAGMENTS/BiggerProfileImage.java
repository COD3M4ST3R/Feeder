package com.mysocialmediaappfeeder.social.FRAGMENTS;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mysocialmediaappfeeder.social.R;



public class BiggerProfileImage extends Fragment
{
    View view;

    //  VARIABLES
    String image;

    //  XML
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view =  inflater.inflate(R.layout.fragment_bigger_profile_image, container, false);

        //  GETTING BUNDLE FROM SEARCHEDFRAGMENT
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            image = bundle.getString("image");
        }

        //  CASTING XML
        imageView = view.findViewById(R.id.imageView);

        if(image != null)
        {
            Glide.with(getContext()).load(image).into(imageView);
        }

        return view;
    }

}
