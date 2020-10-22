package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mysocialmediaappfeeder.social.R;

public class ShareFragment extends Fragment
{
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_share, container, false);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "https://play.google.com/store/apps/details?id=com.mysocialmediaappfeeder.social";
        String shareSub = "Download Now!";
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Share Using"));



        return view;
    }
}
