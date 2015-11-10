package com.example.u2.raksha;

import android.annotation.TargetApi;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class IndoorNavigationActivity extends AppCompatActivity {
    private static final int MAP_ID = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    String status;
    BluetoothLeScanner mLEScanner;
    private List<ScanFilter> filters;
    private ScanSettings settings;
    private int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    public static MapWidget mapWidget;
    private static final long SCAN_PERIOD = 1000000000;
    final ParseUser user = new ParseUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser user;
        setContentView(R.layout.activity_indoor_navigation);
        user = ParseUser.getCurrentUser();
        status = (String) user.get("status");

        mHandler = new Handler();
        downloadTask("1");

    }
    //............................................................................................................

    @Override
    protected void onResume() {
        super.onResume();
        pullMap();
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
        scanLeDevice(true);
    }
    //............................................................................................................

    public void unzipTask(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                Toast.makeText(this.getApplicationContext(),"DONE ZIPPING",Toast.LENGTH_SHORT ).show();
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory()){
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
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
    }
    //............................................................................................................

    private void downloadTask(String i) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_indoor));
        query.whereEqualTo(getString(R.string.parse_building_id), i);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ParseObject result = list.get(0);
                    ParseFile map = result.getParseFile(getString(R.string.parse_building_plan));
                    String url = map.getUrl();
                    final DownloadTask downloadTask = new DownloadTask(IndoorNavigationActivity.this);
                    AsyncTask<String, Integer, String> results = downloadTask.execute(url);
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
/*
            if(btDevice.toString().equalsIgnoreCase("64:5A:04:56:E3:18")){
                Toast.makeText(IndoorNavigationActivity.this.getApplication(),btDevice.toString()+"I m here",Toast.LENGTH_SHORT).show();
            }
*/
            placePointer(300, 800);
            if (btDevice.getName() != null) {
                Toast.makeText(IndoorNavigationActivity.this.getApplication(), btDevice.getName().toString() + " I am here", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(IndoorNavigationActivity.this.getApplication(), btDevice.toString() + " I am here", Toast.LENGTH_SHORT).show();
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
        final long LAYER_ID = 2;
        MapLayer layer = mapWidget.createLayer(LAYER_ID);

        // getting icon from assets
        Drawable icon = getResources().getDrawable(R.drawable.maps_blue_dot);

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

/*    private void pullMap() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("IndoorRegisters");
        query.whereEqualTo("reg", "1");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> dataHolder, ParseException e) {
                if (e == null) {
                    ParseFile plan = (ParseFile) dataHolder.get(0).get("buildingPlan");
                    new DownloadImageTask((TouchImageView) findViewById(R.id.imageView))
                            .execute(plan.getUrl());
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                    Toast.makeText(IndoorNavigationActivity.this, "Connection to Database failed. Check your Internet.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }*/
    //............................................................................................................

/*    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        TouchImageView bmImage;

        public DownloadImageTask(TouchImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }*/

    //............................................................................................................
/*
    private void saveFile(String url, String path){
        File direct = new File(path);

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/Pictures", "plan.jpg");
        mgr.enqueue(request);
        File file = new File(path+"/Pictures/plan.jpg");
        if (file.exists()){
        }

    }*/
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
}
