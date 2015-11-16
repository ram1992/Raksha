package com.example.u2.raksha;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TempActivity extends ActionBarActivity{
    private static final String TAG = "BroadcastTest";
    String status;
    String latitude;
    String longitude;
    ParseUser user;
    TextView text2;
    ImageView image;
    Timer timer;
    TimerTask timerTask;
    LocationManager locationManager;
    LocationListener locationListener;
    private RecyclerView recyclerview;
    final Handler handler = new Handler();
    private List<CardData> cardDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();
        status = (String) user.get(getString(R.string.status));
        setContentView(R.layout.activity_temp);
        //text2 = (TextView) findViewById(R.id.textview_3);
       // image = (ImageView) findViewById(R.id.imageView);
       // text2.setText(R.string.Loading);
/*        recyclerview = (RecyclerView) findViewById(R.id.drawerlist);
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(llm);
        initializeData();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(cardDataList);
        recyclerview.setAdapter(adapter);
        //defineButtons(status);
        //welcomeMessage();*/

    }

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData(){
        cardDataList = new ArrayList<>();
        cardDataList.add(new CardData("Call Police", R.drawable.safe_true));
        cardDataList.add(new CardData("Send Child Information to Police", R.drawable.safe_true));
        cardDataList.add(new CardData("Call Child", R.drawable.safe_true));
        cardDataList.add(new CardData("Show Map", R.drawable.safe_true));
        cardDataList.add(new CardData("Register For Indoor Navigation", R.drawable.safe_true));
    }

/*    private void checkSafety(String status) {
        if (status.equals(getString(R.string.child))) {
            image.setImageResource(R.drawable.safe_true);
        } else {
            if (status.equals("true"))
                image.setImageResource(R.drawable.safe_true);
            else if (status.equals("false"))
                image.setImageResource(R.drawable.safe_false);
            else
                image.setImageResource(R.drawable.safe_false);
        }
    }*/
    //............................................................................................................
/*

    private void defineButtons(String status) {
        Button button;
        Button button2;
        Button button3;
        Button button4;
        Button button5;
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        if (status.equals(getString(R.string.parent))) {
            button = (Button) findViewById(R.id.button);
            button.setText(R.string.call_police);
            button2.setText(R.string.call_child);
            button3.setText(R.string.send_info_police);
            button4.setText(R.string.show_map);
            button5.setText(R.string.register_indoor);
        } else {
            button.setText(R.string.call_police);
            button2.setText(R.string.call_parent);
            button3.setText(R.string.send_info_police);
            button4.setText(R.string.show_map);
            button5.setText(R.string.register_indoor);
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callPolice();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                call();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendText();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showMap();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                indoorNavigation();
            }
        });
    }
*/

    private void indoorNavigation() {
        Intent intent = new Intent(TempActivity.this, IndoorNavigationActivity.class);
        startActivity(intent);
    }
    //............................................................................................................

    private void sendText() {
    }
    //............................................................................................................

/*    public void welcomeMessage() {
        TextView text = (TextView) findViewById(R.id.textview_1);
        text.setText("Hello " + user.getString(getString(R.string.full_name)));
    }*/
    //............................................................................................................

    private void pullLocation() {
        if (status.equals(getString(R.string.child))) {
            // Acquire a reference to the system Location Manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            //set a new Timer
            timer = new Timer();

            //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //initialize the TimerTask's job
                    initializeTimerTask();
                }
            }, 5000, 10000); //


        }

    }
    //............................................................................................................

    private void displayLocation(Location location) {
        latitude = "" + location.getLatitude();
        longitude = "" + location.getLongitude();
        String addressString = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
            }

            addressString = sb.toString().replace("null\n", "");

            Log.e("Address from lat,long ;", addressString);
        } catch (IOException e) {
        }
        text2.setText(addressString + "\n" + "(" + location.getLatitude() + "," + location.getLongitude() + ")");
        updateLocation(location);
    }
    //............................................................................................................

    private void displayLocation(ParseGeoPoint location) {
        latitude = "" + location.getLatitude();
        longitude = "" + location.getLongitude();
        String addressString = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
                sb.append("("+address.getLatitude()+","+address.getLongitude()+")");
            }

            addressString = sb.toString().replace("null\n", "");

            Log.e("Address from lat,long ;", addressString);
        } catch (IOException e) {
        }
        text2.setText(addressString + "\n" + "(" + location.getLatitude() + "," + location.getLongitude() + ")");
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
                    Toast.makeText(TempActivity.this, "Connection to Database failed. Check your Internet.", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }
    //............................................................................................................

    private void showMap() {

        if (latitude != null && longitude != null) {
            Bundle extras = new Bundle();
            Intent intent = new Intent(TempActivity.this, MapsActivity.class);
            extras.putString("latitude", latitude);
            extras.putString("longitude", longitude);
            intent.putExtras(extras);
            startActivity(intent);
        } else {
            final ProgressDialog dialog;
            dialog = ProgressDialog.show(this, "dialog title",
                    "dialog message", true);
            dialog.dismiss();
        }

    }
    //............................................................................................................

    private void call() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:3042168639"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }
    //............................................................................................................

    private void callPolice() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:3042168639"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    //............................................................................................................

    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.
            // Ask for child's locatoin
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Child");
            query.whereEqualTo("parentEmailId", user.getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        // Set up a progress dialog
                        final ProgressDialog dialog = new ProgressDialog(TempActivity.this);
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
                            Toast.makeText(TempActivity.this, "Invalid Parent Email" + " " + list.size(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                        Toast.makeText(TempActivity.this, "Connection to Database failed. Check your Internet.", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
            //Do something to the UI thread here

        }
    };
    //............................................................................................................

    public void initializeTimerTask() {
        this.runOnUiThread(Timer_Tick);
    }
    //.................................................................................................................

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //.................................................................................................................

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            user.logOut();
                            Intent intent = new Intent(TempActivity.this, DispatchActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //.................................................................................................................

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    //.................................................................................................................

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        //pullLocation();
        //checkSafety(status);
    }
    //.................................................................................................................

/*    @Override
    public void onStop() {  // After a pause OR at startup
        super.onStop();
        if (status.equals("Parent")) {
            this.timer.cancel();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }*/
    //.................................................................................................................

 /*   @Override
    public void onPause() {  // After a pause OR at startup
        super.onPause();
        if (status.equals("Parent")) {
            this.timer.cancel();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }
*/

}