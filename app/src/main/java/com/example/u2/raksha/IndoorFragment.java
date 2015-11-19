package com.example.u2.raksha;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by u2 on 11/15/2015.
 */
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class IndoorFragment extends Fragment{
    boolean isRegistered = true;
    public IndoorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_indoor, container, false);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Code executes EVERY TIME user views the fragment
        // Inflate the layout for this fragment
        if(isVisibleToUser && isRegistered){
            Intent intent = new Intent(this.getContext(),IndoorNavigationActivity.class);
            startActivity(intent);
        }
    }
}
