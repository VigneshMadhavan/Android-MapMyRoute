package com.example.vimadhavan.mapmyroute.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimadhavan.mapmyroute.R;
import com.example.vimadhavan.mapmyroute.database.DBhandler;
import com.example.vimadhavan.mapmyroute.model.Track;
import com.example.vimadhavan.mapmyroute.model.TrackAdapter;

import java.util.ArrayList;

public class SavedRoutesActivity extends AppCompatActivity  {

    public ListView allTrackList;
    public TextView defaultText;
    public ArrayList<Track> allTracks;
    public TrackAdapter allTaskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saved_routes);
        setTitle(getString(R.string.saved_route));

        allTrackList = (ListView) findViewById(R.id.allTaskList);
        defaultText=  (TextView) findViewById(R.id.defaultTxt);



        init();
    }

    private void init(){
        allTracks= DBhandler.getInstance(this).getAllTracks();

        if(allTracks.isEmpty()){
            defaultText.setText(getString(R.string.noTracks));

            defaultText.setVisibility(View.VISIBLE);
            allTrackList.setVisibility(View.GONE);
        }else{
            defaultText.setVisibility(View.GONE);

            allTrackList.setVisibility(View.VISIBLE);
        }

        allTaskAdapter=new TrackAdapter(this,allTracks);
        allTrackList.setAdapter(allTaskAdapter);
        allTaskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent completedTaskIntent =new Intent(SavedRoutesActivity.this,MainActivity.class);
        completedTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(completedTaskIntent);
    }
}
