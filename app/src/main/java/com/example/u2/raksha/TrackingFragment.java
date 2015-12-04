package com.example.u2.raksha;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by Ram on 11/15/2015.
 */


public class TrackingFragment extends Fragment {
    private static final String TAG = "BroadcastTest";
    String status;
    String latitude;
    String longitude;
    ParseUser user;
    TextView text1;
    TextView text2;
    ImageView image;
    Timer timer;
    LocationManager locationManager;
    LocationListener locationListener;
    Handler handler;

    public TrackingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();
        status = (String) user.get(getString(R.string.status));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_tracking, container, false);
        text2 = (TextView) myFragmentView.findViewById(R.id.textview_welcome);
        if(status.equalsIgnoreCase("child")){
            text2.setText("Hi " + user.getString("fullName") + "\n\n\nCurrent Location:");
        }else{
            text2.setText("Hi " + user.getString("fullName") + "\n\n\nChild's Current Location:");
        }
        image = (ImageView) myFragmentView.findViewById(R.id.imageView_login_loading);
        text1 = (TextView) myFragmentView.findViewById(R.id.textview_dialog);
        text1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                image.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        handler = new Handler();

        CardView card = (CardView) myFragmentView.findViewById(R.id.card_view);
        card.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                showMap();
                return true;
            }
        });
        return myFragmentView;
    }

    private void pullLocation() {
        if (status.equals(getString(R.string.child))) {
            // Acquire a reference to the system Location Manager
            locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    displayLocation(location);

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

// Register the listener with the Location Manager to receive location updates
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            //set a new Timer
            timer = new Timer();
            handler.postDelayed(runnable, 10000);
        }

    }
    //............................................................................................................

    private void displayLocation(Location location) {
        latitude = "" + location.getLatitude();
        longitude = "" + location.getLongitude();
        String addressString = null;
        try {
            Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                sb.append(address.getUrl().trim()).append("\n");
                sb.append(address.getThoroughfare()).append("\n");
                sb.append(address.getSubLocality()).append("\n");
                sb.append(address.getSubAdminArea()).append("\n");
                sb.append(address.getPremises()).append("\n");
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getCountryName()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
            }

            addressString = sb.toString().trim().replace("null\n", "");

            Log.e("Address from lat,long ;", addressString);
        } catch (IOException e) {
        }
        text1.setText(addressString + "\n" + "(" + location.getLatitude() + "," + location.getLongitude() + ")");
        updateLocation(location);
    }
    //............................................................................................................

    private void updateLocation(final Location location) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Child");
        query.whereEqualTo("emailId", user.getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ParseObject currentLocation = list.get(0);
                    ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    currentLocation.put("currentLocation", point);
                    currentLocation.saveInBackground();

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                    Toast.makeText(TrackingFragment.this.getContext(), "Connection to Database failed. Check your Internet.", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }
    //............................................................................................................

    private void displayLocation(ParseGeoPoint location) {
        latitude = "" + location.getLatitude();
        longitude = "" + location.getLongitude();
        String addressString = null;
        try {
            Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                sb.append(address.getThoroughfare()).append("\n");
                sb.append(address.getSubLocality()).append("\n");
                sb.append(address.getSubAdminArea()).append("\n");
                sb.append(address.getPremises()).append("\n");
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getCountryName()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                //sb.append("("+address.getLatitude()+","+address.getLongitude()+")");
            }

            addressString = sb.toString().replace("null\n", "");

            Log.e("Address from lat,long ;", addressString);
        } catch (IOException e) {
        }
        text1.setText(addressString + "\n" + "(" + location.getLatitude() + "," + location.getLongitude() + ")");
    }

    //.................................................................................................................

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        pullLocation();
        //checkSafety(status);
    }
//.................................................................................................................

    @Override
    public void onStop() {  // After a pause OR at startup
        super.onStop();
        if (status.equals("Parent")) {
            this.timer.cancel();
        } else {
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(locationListener);
                return;
            }

        }
    }
//.................................................................................................................

    @Override
    public void onPause() {  // After a pause OR at startup
        super.onPause();
        if (status.equals("Parent")) {
            this.timer.cancel();
        } else {
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            //This method runs in the same thread as the UI.
            // Ask for child's locatoin
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Child");
            query.whereEqualTo("parentEmailId", user.getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        // Set up a progress dialog
                        final ProgressDialog dialog = new ProgressDialog(TrackingFragment.this.getContext());
                        dialog.setMessage(getString(R.string.progress_pulling_locaion_parent));
                        dialog.show();
                        dialog.dismiss();
                        Log.d("Users", "Retrieved " + list.size() + " users");
                        if (list.size() > 0) {
                            ParseGeoPoint location = list.get(0).getParseGeoPoint("currentLocation");
                            displayLocation(location);
                            Boolean safe = list.get(0).getBoolean("isSafe");
                            if (safe != null) {
//                                checkSafety(Boolean.toString(safe));
                            }
                        } else {
                            Toast.makeText(TrackingFragment.this.getContext(), "Invalid Parent Email" + " " + list.size(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                        Toast.makeText(TrackingFragment.this.getContext(), "Connection to Database failed. Check your Internet.", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
            //Do something to the UI thread here
      /* and here comes the "trick" */
            handler.postDelayed(this, 10000);
        }
    };

    //............................................................................................................

    private void showMap() {

        if (latitude != null && longitude != null) {
            Bundle extras = new Bundle();
            Intent intent = new Intent(this.getContext(), MapsActivity.class);
            extras.putString("latitude", latitude);
            extras.putString("longitude", longitude);
            intent.putExtras(extras);
            startActivity(intent);
        } else {
            final ProgressDialog dialog;
            dialog = ProgressDialog.show(this.getContext(), "dialog title",
                    "dialog message", true);
            dialog.dismiss();
        }

    }
}