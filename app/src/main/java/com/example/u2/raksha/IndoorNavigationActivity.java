package com.example.u2.raksha;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class IndoorNavigationActivity extends AppCompatActivity implements TaskListener {
    private static final int MAP_ID = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    String status;
    private BluetoothLeScanner mLEScanner;
    private List<ScanFilter> filters;
    private ScanSettings settings;
    private int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    public static MapWidget mapWidget;
    private static final long SCAN_PERIOD = 1000000000;
    final ParseUser user = new ParseUser();
    String bNum ="";
    TaskListener taskListener;
    String lastbNum = "";
    private TextView textView;
    ArrayList<String> bluetoothPosition;
    ArrayList<String> bluetoothId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser user;
        setContentView(R.layout.activity_indoor_navigation);
        user = ParseUser.getCurrentUser();
        status = (String) user.get("status");
        mHandler = new Handler();
        taskListener = this;
        Intent myIntent = getIntent();
        bNum = myIntent.getStringExtra("buildingNumber");
        if(bNum!= null) {
            if(!bNum.isEmpty()){
                downloadTask(bNum);
            }
            else{
                pullMap();
            }
        }

    }
    //............................................................................................................

    @Override
    protected void onResume() {
        super.onResume();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.message_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

    }
    //............................................................................................................

    private void pullMap() {
        File map = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/media/com.Raksha/map");
        mapWidget = new MapWidget(IndoorNavigationActivity.this,map);
        mapWidget.setUseSoftwareZoom(true);
        mapWidget.setSaveEnabled(true);
        mapWidget.setMinZoomLevel(10);
        mapWidget.setMaxZoomLevel(16);
        mapWidget.getConfig().setZoomBtnsVisible(true);
        LinearLayout layout = (LinearLayout) findViewById(R.id.indoor);
        layout.setBackgroundColor(0xFFFFFFFF);
        layout.addView(mapWidget);
        scanLeDevice(true);
    }
    //............................................................................................................

    private void downloadTask(String buildingNum) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_indoor));
        query.whereEqualTo(getString(R.string.parse_building_id), buildingNum);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseObject result = list.get(0);
                        ParseFile map = result.getParseFile(getString(R.string.parse_building_plan));
                        bluetoothId = (ArrayList<String>) result.get("bluetoothId");
                        bluetoothPosition = (ArrayList<String>) result.get("bluetoothPosition");
                        String url = map.getUrl();
                        final DownloadTask downloadTask = new DownloadTask(IndoorNavigationActivity.this, taskListener);
                        AsyncTask<String, Integer, String> results = downloadTask.execute(url);
                    } else {
                        Intent intent = new Intent(IndoorNavigationActivity.this,MainActivity.class);
                        Toast.makeText(IndoorNavigationActivity.this,"Invalid BUilding ID",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                    Toast.makeText(IndoorNavigationActivity.this, R.string.error_internet_connection_database, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    //............................................................................................................

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }

    }
    //............................................................................................................

    // Device scan callback.
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            if(bluetoothId.size() > 0){
                int i = bluetoothId.indexOf(btDevice.toString());
                String loc = bluetoothPosition.get(i);
                Toast.makeText(IndoorNavigationActivity.this.getApplicationContext(),loc,Toast.LENGTH_LONG).show();
                String[] blLoc = new String[2];
                blLoc = loc.split(",");
                placePointer(Integer.parseInt(blLoc[0]),Integer.parseInt(blLoc[1]));
            }

            if (btDevice.getName() != null) {
                //Toast.makeText(IndoorNavigationActivity.this.getApplication(), btDevice.getName().toString() + " I am here", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(IndoorNavigationActivity.this.getApplication(), btDevice.toString() + " I am here", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };
    //............................................................................................................

    private void placePointer(int x, int y) {
        // create map layer with specified ID
        final long LAYER_ID = 5;
        MapLayer layer = mapWidget.createLayer(LAYER_ID);

        // getting icon from assets
        Drawable icon = ContextCompat.getDrawable(this,R.drawable.indoor_location);

        // set ID for the object
        final long OBJ_ID = 25;

        // adding object to layer
        MapObject obj = new MapObject(OBJ_ID, icon, new Point(x, y), PivotFactory.createPivotPoint(icon, PivotFactory.PivotPosition.PIVOT_CENTER), true, false);
        // obj.setCaption("5434 KNNB");

        //MapObject(OBJ_ID, icon, new Point(x, y), PivotFactory.createPivotPoint(icon, PivotPosition.PIVOT_CENTER), true, false)

        layer.addMapObject(obj);
    }
    //............................................................................................................

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            if (device.getName() != null) {
                                Toast.makeText(IndoorNavigationActivity.this.getApplication(), device.getName().toString() + "I m here", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(IndoorNavigationActivity.this.getApplication(), device.toString() + "I m here", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };

    //............................................................................................................

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }
    //............................................................................................................

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }
    //............................................................................................................
/*
    public void onBackPressed() {
        Intent intent = new Intent(IndoorNavigationActivity.this,MainActivity.class);
        startActivity(intent);
    }
*/

    @Override
    public void taskComplete(ArrayList<String> list) {
        if (list.size() != 0) {
            Log.i("Log", "list  " + list.get(0));
        }
        pullMap();
    }
}
