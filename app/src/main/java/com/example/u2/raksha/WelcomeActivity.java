package com.example.u2.raksha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity which displays a registration screen to the user.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Parent Log in button click handler
        Button loginParentButton = (Button) findViewById(R.id.login_parent_button);
        loginParentButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(WelcomeActivity.this, LoginParentActivity.class));
            }
        });

        // Log in button click handler
        Button loginChildButton = (Button) findViewById(R.id.login_child_button);
        loginChildButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(WelcomeActivity.this, LoginChildActivity.class));
            }
        });

        // Parent Sign up button click handler
        Button signupParentButton = (Button) findViewById(R.id.signup_parent_button);
        signupParentButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(WelcomeActivity.this, SignUpParentActivity.class));
            }
        });

        // Child Sign up button click handler
        Button signupChildButton = (Button) findViewById(R.id.signup_child_button);
        signupChildButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(WelcomeActivity.this, SignUpChildActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }
}

