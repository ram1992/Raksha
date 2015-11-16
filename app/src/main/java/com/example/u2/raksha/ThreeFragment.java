package com.example.u2.raksha;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by u2 on 11/15/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class ThreeFragment extends Fragment{
    boolean isRegistered = true;
    public  ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_three, container, false);
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
