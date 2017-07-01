package com.example.vimadhavan.mapmyroute.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.vimadhavan.mapmyroute.activity.NewRouteAcitivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by vimadhavan on 6/11/2017.
 */

public class GPSTracker extends Activity implements LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ResultCallback<LocationSettingsResult> {

    private final NewRouteAcitivity mContext;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    // flag for GPS status
    public boolean isGPSEnabled = false;
    // flag for network status
    public boolean isNetworkEnabled = false;
    // flag for GPS status
    public boolean canGetLocation = false;
    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude
    // The minimum distance to change Updates in meters
    public static int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 2*1000; // 2 seconds
    // The minimum time between updates in milliseconds
    private static final long INTERVAL = 5*1000; // 5 seconds
    public final static int REQUEST_LOCATION = 199;

    public GPSTracker(NewRouteAcitivity mContext) {
        this.mContext = mContext;

        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.d("Debug:Vignesh", "isGPSEnabled=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = isWifiConnected() || isGprsConnected();

            Log.d("Debug:Vignesh", "isNetworkEnabled=" + isNetworkEnabled);

            if (isGPSEnabled==false) {
                // no network provider is enabled
                //showSettingsAlert();

                enableLoc();
            }else if(isNetworkEnabled==false){
                showSettingsAlert();
            }else {
                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                if (isNetworkEnabled) {
                    location = null;

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("Debug:Vignesh", "Network");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            this.canGetLocation = true;
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services

                if (isGPSEnabled) {
                    location=null;
                    if (location == null) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Log.d("Debug:Vignesh", "GPS Enabled--1");
                            if (location != null) {
                                Log.d("Debug:Vignesh", "GPS Enabled");
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                this.canGetLocation = true;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void enableLoc() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();



            //
        }

    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(MIN_TIME_BW_UPDATES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("Internet");

        // Setting Dialog Message
        alertDialog
                .setMessage("Please enable Internet.");

        // On pressing Settings button




        // on pressing cancel button
        alertDialog.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }



    public boolean isWifiOn() {

        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();

        Log.d("Debug:isWifiOn", String.valueOf(wifiEnabled));

        return wifiEnabled;
    }


    public boolean isWifiConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;

        //Log.d("Debug:isWIFIConnected", String.valueOf(wifiConnected));

        return wifiConnected;
    }


    public boolean isGprsConnected() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;

        //Log.d("Debug:ISGPRSConnected", String.valueOf(mobileConnected));

        return mobileConnected;
    }



    @Override
    public void onLocationChanged(Location newLocation) {
        this.location=newLocation;
        if (location != null) {
            this.canGetLocation=true;
            mContext.onLocationChanged(location);

        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Debug:", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Debug:", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Debug:", "onProviderDisabled");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Debug:", "onConnected");
        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Debug:", "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Location error","Location error " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(mContext, REQUEST_LOCATION);
                    Log.d("Debug:", "onResult");

                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
        }
    }
}
