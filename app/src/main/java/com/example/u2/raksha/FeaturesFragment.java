package com.example.u2.raksha;

import android.support.v4.app.Fragment;

/**
 * Created by u2 on 11/15/2015.
 */
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class FeaturesFragment extends Fragment{
    ParseUser user;
    String status;
    private RecyclerView recyclerview;
    private List<CardData> cardDataList;
    public FeaturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = ParseUser.getCurrentUser();
        status = (String) user.get(getString(R.string.status));
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_features, container, false);
        recyclerview = (RecyclerView)myFragmentView.findViewById(R.id.drawerlist);
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        recyclerview.setLayoutManager(llm);
        initializeData();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(cardDataList,this.getContext());
        recyclerview.setAdapter(adapter);
        return myFragmentView;
    }

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData(){
        cardDataList = new ArrayList<>();
        cardDataList.add(new CardData("Call Police", R.drawable.safe_true));
        cardDataList.add(new CardData("Send Child Information to Police", R.drawable.safe_true));
        if (status.equalsIgnoreCase("child")){
            cardDataList.add(new CardData("Call Parent", R.drawable.safe_true));
        }else{
            cardDataList.add(new CardData("Call Child", R.drawable.safe_true));
        }
        cardDataList.add(new CardData("Register For Indoor Navigation", R.drawable.safe_true));
    }

}
