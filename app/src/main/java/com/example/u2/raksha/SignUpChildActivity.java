package com.example.u2.raksha;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.Serializable;

public class SignUpChildActivity extends Activity {
    // UI references.
    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText parentUsernameEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;
    private EditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_childsignup);

        // Set up the signup form.
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        parentUsernameEditText = (EditText) findViewById(R.id.parent_username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);

        // Set up the submit button click handler
        Button mActionButton = (Button) findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });
    }

    private void signup() {
        String name = nameEditText.getText().toString();
        final String username = usernameEditText.getText().toString().trim();
        final String parentUsername = parentUsernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (name.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_name));
        }
        if (username.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (parentUsername.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_ivalid_phone));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignUpChildActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(SignUpChildActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

       // Set up a new Parse user for child
        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username);
        user.put("status", "Child");
        user.put("fullName", name);
       // user.put("phoneNumber", Integer.parseInt(phoneNumber));

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    // Hooray! Let them use the app now.
                    // Set up a progress dialog
                    ParseObject child = new ParseObject("Child");
                    child.put("emailId", username);
                    child.put("parentEmailId", parentUsername);

                    child.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Saved successfully.
                                // Start an intent for the dispatch activity
                                Intent intent = new Intent(SignUpChildActivity.this, DispatchActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            } else {
                                // The save failed.
                                Toast.makeText(SignUpChildActivity.this, e.toString(), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });

                } else {
                    // . Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(getApplicationContext(), "Sign up didn't succeed" + "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}