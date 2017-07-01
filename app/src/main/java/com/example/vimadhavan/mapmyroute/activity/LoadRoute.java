package com.example.vimadhavan.mapmyroute.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.vimadhavan.mapmyroute.R;
import com.example.vimadhavan.mapmyroute.model.Track;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class LoadRoute extends AppCompatActivity implements OnMapReadyCallback {
    private TextView msg,txtBg;
    private GoogleMap googleMap;
    private Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_route);
        msg= (TextView) findViewById(R.id.msg1);
        txtBg= (TextView) findViewById(R.id.textViewBg1);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.savedMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap=map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        track=new Track();
        track.setTitle(getIntent().getStringExtra("Title"));
        track.setDate(getIntent().getStringExtra("Date"));
        track.setSpeed(getIntent().getStringExtra("Speed"));
        track.setTime(getIntent().getStringExtra("Time"));
        track.setPath(getIntent().getStringExtra("Path"));
        track.setDistance(getIntent().getStringExtra("Distance"));
        Log.d("Debug:Path:",track.getPath());
        String LAT=track.getPath().split("\\|")[0];
        String LON=track.getPath().split("\\|")[1];
        Log.d("Debug:LAT:",LAT);
        Log.d("Debug:LON:",LON);
        String[] LatArr=LAT.split(",");
        String[] LonArr=LON.split(",");



        PolylineOptions retriveLines=new PolylineOptions();

        float maxDistance= (float) 0.0;
        Log.d("Debug:LatArr:",LatArr[0]);
        Log.d("Debug:LonArr:",LonArr[0]);
        for(int i=0;i<LatArr.length;i++){
           LatLng retrive=new LatLng(Double.valueOf(LatArr[i]).doubleValue(),Double.valueOf(LonArr[i]).doubleValue());

            if(i==0){

                MarkerOptions startMarkerOption=new MarkerOptions();
                startMarkerOption.position(retrive);
                startMarkerOption.title("Start");
                startMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                googleMap.addMarker(startMarkerOption);

            }else if (i==LatArr.length-1){

                MarkerOptions endMarkerOption=new MarkerOptions();
                endMarkerOption.position(retrive);
                endMarkerOption.title("End");
                // startingPoint.snippet("Snippet");
                endMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                googleMap.addMarker(endMarkerOption);

            }else if(i!=0){
                Location current_location=new Location("current");
                current_location.setLatitude(Double.valueOf(LatArr[i]).doubleValue());
                current_location.setLongitude(Double.valueOf(LonArr[i]).doubleValue());

                Location previous_location=new Location("previous");
                previous_location.setLatitude(Double.valueOf(LatArr[0]).doubleValue());
                previous_location.setLongitude(Double.valueOf(LonArr[0]).doubleValue());

                float distance=previous_location.distanceTo(current_location);

                if(distance>maxDistance){
                    maxDistance=distance;
                }


            }
            retriveLines.add(retrive);

        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        int color_val= Integer.parseInt(sp.getString("list_preference_1","1"));



        Log.d("Debug::val:", String.valueOf(color_val));

        switch (color_val){
            case 1:
                googleMap.addPolyline(retriveLines).setColor(Color.RED);
                break;
            case 2:
                googleMap.addPolyline(retriveLines).setColor(Color.GREEN);
                break;
            case 3:
                googleMap.addPolyline(retriveLines).setColor(Color.BLACK);
                break;
            default:
                googleMap.addPolyline(retriveLines).setColor(Color.RED);
                break;
        }





        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(midPoint(Double.valueOf(LatArr[0]).doubleValue(),Double.valueOf(LonArr[0]).doubleValue(),Double.valueOf(LatArr[LatArr.length-1]).doubleValue(),Double.valueOf(LonArr[LonArr.length-1]).doubleValue()) ,getZoomLevel(maxDistance)) );
        setTitle(track.getTitle());


        String show="Distance:"+track.getDistance()+"\nSpeed:"+track.getSpeed()+"\nDuration:"+track.getTime();
        updateDashBoard(show);
       //CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(LatArr[0]).doubleValue(),Double.valueOf(LonArr[0]).doubleValue()));
       // CameraUpdate zoom=CameraUpdateFactory.zoomIn();

      // googleMap.animateCamera(zoom);
        //googleMap.animateCamera(center);
    }

    private void updateDashBoard(String msgTxt){
        msg.setText(msgTxt);
        txtBg.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        Log.d("Debug:","updateDashBoard::"+msgTxt);
    }

    private LatLng midPoint(double lat1,double lon1,double lat2,double lon2){

        double lat3=(lat1+lat2)/2;
        double lon3=(lon1+lon2)/2;

        return new LatLng(lat3,lon3);
    }

    private float getZoomLevel(double radius){
        double scale = radius / 500;
        return ((float) (16 - Math.log(scale) / Math.log(2)));
    }
}
