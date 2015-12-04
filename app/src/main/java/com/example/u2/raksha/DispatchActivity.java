package com.example.u2.raksha;

/**
 * Created by u2 on 9/30/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in (TempActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
                    Intent intent = new Intent(DispatchActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
            }else {
            handler.postDelayed(new Runnable() {

                public void run() {
                    // Start and intent for the logged out activity
                    startActivity(new Intent(DispatchActivity.this, WelcomeActivity.class));

                }
            }, 1000);
        }
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
    }

}
