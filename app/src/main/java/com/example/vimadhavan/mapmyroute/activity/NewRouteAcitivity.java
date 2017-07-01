package com.example.vimadhavan.mapmyroute.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimadhavan.mapmyroute.utils.GPSTracker;
import com.example.vimadhavan.mapmyroute.MyInterface;
import com.example.vimadhavan.mapmyroute.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewRouteAcitivity extends AppCompatActivity implements MyInterface, OnMapReadyCallback, View.OnClickListener {

    private GPSTracker tracker;
    private TextView msg, txtBg;
    private Toast msgToast;
    private static final int RequestPermissionCode = 1;
    private boolean isStarted = false;
    private boolean isMarked = false;
    private GoogleMap googleMap;
    private static float ZOOM = 18;
    private Marker movingPoint;
    private Marker startingPoint;
    private Marker endingPoint;
    private MarkerOptions moveMarkerOption;
    private MarkerOptions startMarkerOption;
    private MarkerOptions endMarkerOption;
    private ImageButton startBtn;
    private ImageButton stopBtn;
    private LatLng startLatLng;
    private LatLng stopLatLng;
    private PolylineOptions lines;
    private long endTime, startTime;
    private ArrayList<LatLng> path = new ArrayList<LatLng>();
    private int totalDistance = 0;
    private CountDownTimer timer;
    private int timInMins_noResponse = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.vimadhavan.mapmyroute.R.layout.activity_new_route);
        msg = (TextView) findViewById(R.id.msg);
        txtBg = (TextView) findViewById(R.id.textViewBg);
        startBtn = (ImageButton) findViewById(R.id.startBtn);
        stopBtn = (ImageButton) findViewById(R.id.stopBtn);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        setTitle(getString(R.string.new_route));


        EnableRuntimePermission();
    }


    private void sendMsg(String msg) {
        if (msgToast != null) {
            msgToast.cancel();
        }
        msgToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        msgToast.show();
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(NewRouteAcitivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            sendMsg("Please enable LOCATION permission allows us to acccess your LOCATION");


        } else {

            ActivityCompat.requestPermissions(NewRouteAcitivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);

        }
    }


    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    sendMsg("Permission Granted, now your application can access LOCATION.");
                    init();

                } else {

                    sendMsg("Permission Canceled, now your application cannot access LOCATION.");

                }
                break;
        }
    }

    private void setUpMapIfNeeded() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.newMap);
        mapFragment.getMapAsync(this);
    }

    private void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);


    }

    private void startMap() {
        Log.d("Debug:", "startMap");
        tracker = new GPSTracker(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int dist = Integer.parseInt(sp.getString("edit_text_preference_1", "1"));
        GPSTracker.MIN_DISTANCE_CHANGE_FOR_UPDATES = dist;
        setUpMapIfNeeded();
        if (tracker.canGetLocation()) {


        } else {
            msg.setText("Error:startMap");
        }

    }

    private void addMarker() {

        if (tracker.canGetLocation()) {
            Log.d("Debug:", "addMarker");
            updateCamera();
            moveMarkerOption = new MarkerOptions();
            moveMarkerOption.position(new LatLng(tracker.getLatitude(), tracker.getLongitude()));
            moveMarkerOption.title("Yor are here");

            // startingPoint.snippet("Snippet");
            moveMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            movingPoint = googleMap.addMarker(moveMarkerOption);
            isMarked = true;
        }

    }

    private void updateMarker() {
        if (tracker.canGetLocation()) {
            Log.d("Debug:", "updateMarker");
            updateCamera();
            movingPoint.setPosition(new LatLng(tracker.getLatitude(), tracker.getLongitude()));

        }

    }

    private void fixStartingPoint() {

        if (tracker.canGetLocation()) {
            Log.d("Debug:", "fixStartingPoint");
            startBtn.setVisibility(View.GONE);
            //

            updateCamera();
            startLatLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
            startMarkerOption = new MarkerOptions();
            startMarkerOption.position(startLatLng);
            startMarkerOption.title("Start");
            startMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            startingPoint = googleMap.addMarker(startMarkerOption);
            //startingPoint.setPosition(startLatLng);
            //startingPoint.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            lines = new PolylineOptions();
            path = new ArrayList<LatLng>();
            totalDistance = 0;

            timer = new CountDownTimer(1000 * 60 * timInMins_noResponse, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String show = "Distance:" + getDistance() + "\nSpeed:" + getSpeed() + "\nDuration:" + getTime();
                    updateDashBoard(show);
                }

                @Override
                public void onFinish() {
                    finish();
                }
            }.start();


            lines.add(startLatLng);
            path.add(startLatLng);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int color_val = Integer.parseInt(sp.getString("list_preference_1", "1"));

            switch (color_val) {
                case 1:
                    googleMap.addPolyline(lines).setColor(Color.RED);
                    break;
                case 2:
                    googleMap.addPolyline(lines).setColor(Color.GREEN);

                    break;
                case 3:
                    googleMap.addPolyline(lines).setColor(Color.BLACK);

                    break;
                default:
                    googleMap.addPolyline(lines).setColor(Color.RED);
                    break;
            }
            isStarted = true;
            stopBtn.setVisibility(View.VISIBLE);
            String show = "";
            startTime = SystemClock.elapsedRealtime();
            show = "Distance:" + getDistance() + "\nSpeed:" + getSpeed() + "\nTime:" + getTime();
            updateDashBoard(show);

            txtBg.setVisibility(View.VISIBLE);
            msg.setVisibility(View.VISIBLE);

        }

    }

    private String getDistance() {
        String dist = "0 Meter";
        Log.d("Debug:", "getDistance::" + path.size());
        if (path.size() > 1) {
            //dist= getDistanceBtw(path.get(0),path.get(path.size()-1))+" Meter";
            dist = totalDistance + " Meter";

        }

        return dist;
    }

    private int getChangedDistance(LatLng newLatLng) {
        int dist = 0;

        if (path.size() > 0) {
            dist = getDistanceBtw(path.get(path.size() - 1), newLatLng);

        }

        return dist;
    }

    private int getDistanceBtw(LatLng start, LatLng end) {


        Location from = new Location("Start");
        from.setLatitude(start.latitude);
        from.setLongitude(start.longitude);

        Location to = new Location("End");
        to.setLatitude(end.latitude);
        to.setLongitude(end.longitude);

        from.distanceTo(to);


        return (int) from.distanceTo(to);
    }

    private String getSpeed() {
        String speed = "0.00 Meter/Min";

        if (path.size() > 1) {
            DecimalFormat df = new DecimalFormat("####.##");
            double time = (double) (((SystemClock.elapsedRealtime() - startTime) / 1000) % 3600) / 60;
            double S = (double) (totalDistance / time);
            speed = (df.format(S)) + " Meter/Min";

            Log.d("Debug:speed", totalDistance + "/" + time);
        }

        return speed;
    }

    private String getTime() {

        return convertSeconds((int) ((SystemClock.elapsedRealtime() - startTime) / 1000));
    }

    private String convertSeconds(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
    }

    private void updateTrack() {
        if (tracker.canGetLocation()) {
            updateCamera();
            LatLng updatedLatLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());


            int changeDis = getChangedDistance(updatedLatLng);

            if (changeDis < 0) {
                changeDis = -1 * changeDis;
            }
            Log.d("Debug:", totalDistance + ":::updateTrack:::" + changeDis);
            if (changeDis > 0) {
                timer.cancel();
                timer.start();


                totalDistance = totalDistance + changeDis;
                movingPoint.setPosition(updatedLatLng);
                lines.add(updatedLatLng);
                path.add(updatedLatLng);

                Log.d("Debug:", "updateTrack:222::" + totalDistance);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                int color_val = Integer.parseInt(sp.getString("list_preference_1", "1"));

                switch (color_val) {
                    case 1:
                        googleMap.addPolyline(lines).setColor(Color.RED);
                        break;
                    case 2:
                        googleMap.addPolyline(lines).setColor(Color.GREEN);

                        break;
                    case 3:
                        googleMap.addPolyline(lines).setColor(Color.BLACK);

                        break;
                    default:
                        googleMap.addPolyline(lines).setColor(Color.RED);
                        break;
                }
                String show = "Distance:" + getDistance() + "\nSpeed:" + getSpeed() + "\nTime:" + getTime();

                updateDashBoard(show);
            }


        }

    }

    private void stopTrackingAndSave() {
        if (tracker.canGetLocation()) {
            Log.d("Debug:", "stopTrackingAndSave");
            timer.cancel();
            //stopBtn.setVisibility(View.GONE);
            updateCamera();
            stopLatLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());
            endMarkerOption = new MarkerOptions();
            endMarkerOption.position(stopLatLng);
            endMarkerOption.title("End");
            // startingPoint.snippet("Snippet");
            endMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            endingPoint = googleMap.addMarker(endMarkerOption);
            endingPoint.setPosition(stopLatLng);
            lines.add(stopLatLng);
            path.add(stopLatLng);

            movingPoint.remove();
            tracker.stopUsingGPS();
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception er) {

            }

            String LAT = "";
            String LON = "";

            for (LatLng latLng : path) {
                LAT = LAT + String.valueOf(latLng.latitude) + ",";
                LON = LON + String.valueOf(latLng.longitude) + ",";

            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int color_val = Integer.parseInt(sp.getString("list_preference_1", "1"));

            switch (color_val) {
                case 1:
                    googleMap.addPolyline(lines).setColor(Color.RED);
                    break;
                case 2:
                    googleMap.addPolyline(lines).setColor(Color.GREEN);

                    break;
                case 3:
                    googleMap.addPolyline(lines).setColor(Color.BLACK);

                    break;
                default:
                    googleMap.addPolyline(lines).setColor(Color.RED);
                    break;
            }





            String[] LatArr = LAT.split(",");
            String[] LonArr = LON.split(",");




            Intent savedRoutes = new Intent(this, PopupActivity.class);
            savedRoutes.putExtra("Distance", getDistance());
            savedRoutes.putExtra("Speed", getSpeed());
            savedRoutes.putExtra("Time", getTime());
            savedRoutes.putExtra("Path", LAT.toString() + "|" + LON.toString());
            Date date = new Date();
            savedRoutes.putExtra("Date", date.toString());
            Log.d("Debug:Save:", savedRoutes.getStringExtra("Path"));
            startActivity(savedRoutes);








        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Debug:", "onStart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Debug:", "onDestroy");
        try {
            unregisterReceiver(broadcastReceiver);
            timer.cancel();

        } catch (Error er) {

        } catch (Exception ex) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Debug:", "onStop");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GPSTracker.REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Log.d("Debug:", "RESULT_CANCELED");
                        // msg.setText("Please enable GPS and Net."+tracker.isGprsConnected());
                        break;
                    }
                    default: {
                        Log.d("Debug:", "RESULT_SUCCESS");
                        tracker.getLocation();


                        break;
                    }
                }
                break;
        }

    }


    @Override
    public void onLocationChanged(Location newLocation) {
        Log.d("Debug:onLocationChanged", tracker.canGetLocation() + "::Lat:" + newLocation.getLatitude() + " Lon:" + newLocation.getLongitude() + ":::" + isStarted);

        if (tracker.canGetLocation()) {
            if (!isMarked) {
                addMarker();
            } else if (!isStarted) {
                updateMarker();
            } else {
                updateTrack();
            }

        } else {
            updateDashBoard("Error:onLocationChanged");
        }


    }

    private void updateDashBoard(String msgTxt) {
        msg.setText(msgTxt);
        Log.d("Debug:", "updateDashBoard::" + msgTxt);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Debug:", "onReceive::" + intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));
            final String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    // wifi is disabled
                    Log.d("Debug:", "net is Disabled");
                } else {

                    // wifi is enabled
                    Log.d("Debug:", "net is enabled");

                }
                startMap();
            } else {
                Log.d("Debug:", "ELSE");
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("Debug:", "onMapReady");
        this.googleMap = map;

        if(!isMarked){
            addMarker();
        }else if(!isStarted){
            updateMarker();
        }else{
            updateTrack();
        }

    }

    private void updateCamera(){
        Log.d("Debug:","UpdateCamera:0");
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(tracker.getLatitude(),tracker.getLongitude()));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(ZOOM);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
/*
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(NewRouteAcitivity.this, Locale.getDefault());

        try {
            Log.d("Debug:","UpdateCamera:0");
            addresses = geocoder.getFromLocation(tracker.getLatitude(), tracker.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            Log.d("Debug:","UpdateCamera:1");
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Log.d("Debug:","UpdateCamera:2");
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            String show=  city+"\n"+state+"\n"+country+"\n"+postalCode;

            //updateDashBoard(show);
            Log.d("Debug:","UpdateCamera::"+show);
            //Toast.makeText(NewRouteAcitivity.this, show, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Debug:","UpdateCamera:Erro:"+e.getMessage());
        }*/


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

          case R.id.startBtn:
                fixStartingPoint();
                break;

           case R.id.stopBtn:
                stopTrackingAndSave();
                break;


        }
    }
}
