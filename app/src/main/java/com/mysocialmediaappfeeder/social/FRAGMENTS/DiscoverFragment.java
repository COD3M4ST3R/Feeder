package com.mysocialmediaappfeeder.social.FRAGMENTS;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mysocialmediaappfeeder.social.ADAPTERS.SearchAdapter;
import com.mysocialmediaappfeeder.social.CLASSES.User;
import com.mysocialmediaappfeeder.social.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class DiscoverFragment extends Fragment
{
    //  INITIALIZING XML VARIABLES OF "fragment_discover.xml"
    private RecyclerView resultList;
    private EditText editText_search_Bar;
    private ImageButton imageButton_Search;


    //  INITIALIZE VIEW
    View view;

    //  INITIALIZE ADAPTER
    private SearchAdapter searchAdapter;

    //  INITIALIZE DATASET
    private List<User> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_discover, container, false);

        //  CASTING XML VARIABLES of "fragment_discover.xml"
        editText_search_Bar = view.findViewById(R.id.editText_searchBar);
        imageButton_Search = view.findViewById(R.id.imageButton_Search);
        resultList = view.findViewById(R.id.resultList);

        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchAdapter = new SearchAdapter(getContext(), mData);

        resultList.setAdapter(searchAdapter);

        readUser();

        editText_search_Bar.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                searchUser(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mData = new ArrayList<>();

    }

    private void searchUser(String s)
    {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userName").startAt(s).endAt(s+"\uf8ff").limitToLast(10);

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mData.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    User user = dataSnapshot1.getValue(User.class);
                    mData.add(user);
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    private void readUser()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(editText_search_Bar.getText().toString().equals(""))
                {
                    mData.clear();

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        User user = dataSnapshot1.getValue(User.class);
                        mData.add(user);
                    }
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}













