package com.example.berkcirisci.alphafitness;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.text.Text;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;

public class RecordWorkout extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    private static final String DEVICE_NAME = "MyButton";
    private GoogleMap mMap;
    private Chronometer chronometer;
    private DatabaseHelper databaseHelper;
    private Polyline line = null;
    private BluetoothAdapter mBluetoothAdapter;
    private static boolean isRecording = false;
    private static long chronometerBase = -1;
    private Button p1_button;
    private TextView textDistance;

    // This is how, DatabaseHelper can be initialized for future use
    private DatabaseHelper getHelper() {
        return OpenHelperManager.getHelper(this,DatabaseHelper.class);
    }

    private BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("onServiceConnected", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(myDevice.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_workout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        showPermissionDialog();
        initializeBLE();
        p1_button = (Button) findViewById(R.id.button1);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        textDistance = (TextView) findViewById(R.id.textDistance);
        if(isRecording)
        {
            if(chronometer!= null){
            chronometer.setBase(chronometerBase);
            chronometer.start();
            }
            if(p1_button!= null)
                p1_button.setText("Stop Workout");
        }
    }

    private void initializeBLE() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
            return;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            finish();
            return;
        }
        isFound = false;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private boolean isFound;
    private BluetoothDevice myDevice;
    private Context context = this;
    private android.bluetooth.BluetoothGattCallback myBLEGattCallBack = new BluetoothGattCallback() {
    };
    private BroadcastReceiver mGattUpdateReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.v("aaaaaaaaaa",intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if(device.getName()!=null)
                        Log.v("RecordWorkout", device.getName());
                    if(device.getName().equals(DEVICE_NAME)){
                        mBluetoothAdapter.stopLeScan(this);
                        if(!isFound){
                            isFound = false;
                            Log.v("RecordWorkout", "device is found");
                            myDevice = device;
                            bindService(new Intent(context, BluetoothLeService.class), mServiceConnection, BIND_AUTO_CREATE);
                            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                        }
                    }
                }
            };


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void goToProfile(View view) {
        Intent intent = new Intent(RecordWorkout.this, UserProfile.class);
        startActivity(intent);
    }

    public void TextChangeStartButton(View v) {
        if (isRecording) {
            isRecording = false;
            p1_button.setText("Start Workout");
            stopService(new Intent(RecordWorkout.this, MyService.class));
            chronometer.stop();
            long duration = SystemClock.elapsedRealtime() - chronometer.getBase();
            createPolyLine();
            if(mBluetoothLeService != null){
                mBluetoothLeService.writeLedCharacteristic(0);
                mBluetoothLeService.readStepCountCharacteristic();
            }
            float distance = Workout.updateLastWorkout(getHelper(), duration);
            textDistance.setText(String.format("%.3f", distance/1000));
        } else {
            isRecording = true;
            p1_button.setText("Stop Workout");
            if(line != null)
                line.remove();
            if(mBluetoothLeService != null){
                mBluetoothLeService.writeLedCharacteristic(1);
            }

            startWorkoutService();
            chronometerBase = SystemClock.elapsedRealtime();
            chronometer.setBase(chronometerBase);
            chronometer.start();
        }
    }

    private void createPolyLine() {
        ArrayList<LatLng> path = Workout.getLastWorkoutPath(getHelper());
        line = mMap.addPolyline(new PolylineOptions().addAll(path).width(5).color(Color.RED));
        if(!path.isEmpty()){
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for (LatLng point:path) {
                builder.include(point);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }

    private void startWorkoutService() {
        startService(new Intent(RecordWorkout.this, MyService.class));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showPermissionDialog() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        if(mBluetoothLeService != null)
            mBluetoothLeService.disconnect();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
