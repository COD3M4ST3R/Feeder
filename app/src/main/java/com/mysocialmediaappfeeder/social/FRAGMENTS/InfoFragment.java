package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mysocialmediaappfeeder.social.ACTIVITIES.TermsActivity;
import com.mysocialmediaappfeeder.social.R;

public class InfoFragment extends Fragment
{

    View view;

    //  XML VARIABLES
    Button reportProblem;
    Button suggestions;
    Button terms;
    Button updates;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_info, container, false);

        //  XML VARIABLES
        reportProblem = view.findViewById(R.id.reportProblem);
        suggestions = view.findViewById(R.id.suggestions);
        terms = view.findViewById(R.id.terms);
        updates = view.findViewById(R.id.updates);

        //  REPORT PROBLEM
        reportProblem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppCompatActivity appCompatActivity = (AppCompatActivity)view.getContext();
                ReportProblemFragment reportProblemFragment = new ReportProblemFragment();
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, reportProblemFragment).addToBackStack(null).commit();
            }
        });


        // SUGGESTIONS
        suggestions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppCompatActivity appCompatActivity = (AppCompatActivity)view.getContext();
                SuggestionsFragment suggestionsFragment = new SuggestionsFragment();
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, suggestionsFragment).addToBackStack(null).commit();
            }
        });

        terms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), TermsActivity.class);
                startActivity(intent);
            }
        });

        updates.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppCompatActivity appCompatActivity = (AppCompatActivity)view.getContext();
                UpdateFragment updateFragment = new UpdateFragment();
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, updateFragment).addToBackStack(null).commit();
            }
        });



        return view;
    }
}
