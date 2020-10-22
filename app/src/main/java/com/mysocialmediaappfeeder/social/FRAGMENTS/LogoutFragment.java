package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mysocialmediaappfeeder.social.R;
import com.mysocialmediaappfeeder.social.ACTIVITIES.StartActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        FirebaseAuth.getInstance().signOut(); // CLOSE CONNECTION
        open_StartActivity();
    }

    // INTENT
    public void open_StartActivity(){
        Intent intent = new Intent(getActivity(), StartActivity.class);
        startActivity(intent);
    }
}
