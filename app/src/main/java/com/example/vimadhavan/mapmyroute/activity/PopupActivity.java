package com.example.vimadhavan.mapmyroute.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimadhavan.mapmyroute.R;
import com.example.vimadhavan.mapmyroute.database.DBhandler;
import com.example.vimadhavan.mapmyroute.model.Track;

public class PopupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button saveBtn,cancelBtn;
    private Track track;
    private EditText title;
    private TextView dateTxt,distTxt,speedTxt,timeTxt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        title=(EditText) findViewById(R.id.trackTitle);
        dateTxt=(TextView) findViewById(R.id.dateTxt);
        distTxt=(TextView) findViewById(R.id.distTxt);
        speedTxt=(TextView) findViewById(R.id.speedTxt);
        timeTxt=(TextView) findViewById(R.id.timeTxt);


        saveBtn=(Button) findViewById(R.id.saveBtn);
        cancelBtn=(Button) findViewById(R.id.cancelBtn);

        track=new Track();
        track.setDate(getIntent().getStringExtra("Date"));
        track.setSpeed(getIntent().getStringExtra("Speed"));
        track.setTime(getIntent().getStringExtra("Time"));
        track.setPath(getIntent().getStringExtra("Path"));
        track.setDistance(getIntent().getStringExtra("Distance"));

        dateTxt.setText(track.getDate());
        distTxt.setText(track.getDistance());
        speedTxt.setText(track.getSpeed());
        timeTxt.setText(track.getTime());

        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.saveBtn:
                if(title.getText().toString().isEmpty()){
                    Toast.makeText(this, getString(R.string.fillAllDetails), Toast.LENGTH_LONG).show();
                }else{
                    track.setTitle(title.getText().toString());
                    DBhandler.getInstance(this).addTrack(track);
                    finish();
                    Intent savedActivity =new Intent(PopupActivity.this,SavedRoutesActivity.class);
                    startActivity(savedActivity);
                }

                break;

            case R.id.cancelBtn:
                finish();
                break;



        }
    }


}
