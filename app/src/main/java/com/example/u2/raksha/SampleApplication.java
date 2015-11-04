package com.example.u2.raksha;

import android.app.Application;

import com.parse.Parse;
/**
 * Created by u2 on 9/30/2015.
 */

public class SampleApplication extends Application{

    public void onCreate(){
        super.onCreate();
        Parse.initialize(this, "rcRqIahHRm2yCoCITJ9502jP45jgKhftcv5Tg0id", "6uvL61r5hqNfzmbR2LApeqJfhYAT8PNhL2OrKH08");
    }

}
