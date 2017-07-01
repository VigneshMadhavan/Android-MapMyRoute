package com.example.vimadhavan.mapmyroute.activity;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vimadhavan.mapmyroute.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button newBtn,saveBtn,settingBtn;

    private Toast msgToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.example.vimadhavan.mapmyroute.R.layout.activity_main);


        newBtn =(Button) findViewById(R.id.newBtn);
        saveBtn =(Button) findViewById(R.id.savedBtn);
        settingBtn =(Button) findViewById(R.id.settingBtn);

        newBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater M = getMenuInflater();
        M.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.newBtn:
                //sendMsg("newBtn");
                Intent newRoute=new Intent(this,NewRouteAcitivity.class);
                //login.putExtra("userID",userNameTxt.getText().toString());
                startActivity(newRoute);
                break;

            case R.id.savedBtn:
                //sendMsg("savedBtn");
                Intent savedRoutes=new Intent(this,SavedRoutesActivity.class);
                startActivity(savedRoutes);
                break;

            case R.id.settingBtn:
                //sendMsg("settingBtn");
                Intent settingsAct=new Intent(this,SettingActivity.class);
                startActivity(settingsAct);
                break;


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        android.os.Process.killProcess(android.os.Process.myPid());

        return super.onOptionsItemSelected(item);
    }

    private void sendMsg(String msg){
        if(msgToast!=null){
            msgToast.cancel();
        }
        msgToast=Toast.makeText(this,msg,Toast.LENGTH_LONG);
        msgToast.show();
    }



}
