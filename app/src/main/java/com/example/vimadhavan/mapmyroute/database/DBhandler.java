package com.example.vimadhavan.mapmyroute.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.vimadhavan.mapmyroute.model.Track;
import com.example.vimadhavan.mapmyroute.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by vimadhavan on 4/9/2017.
 */

public class DBhandler {
    private SQLiteDatabase db;
    private final Context context;
    private final DBhelper dbHelper;
    private static DBhandler db_handler = null;

    public static DBhandler getInstance(Context context){
        try{
            if(db_handler == null){
                db_handler = new DBhandler(context);

            }
            db_handler.open();
        }catch(IllegalStateException e){
            //db_helper already open
        }
        return db_handler;
    }

    public DBhandler(Context context) {

        this.context = context;
        this.dbHelper = new DBhelper(context, Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
    }
    public void close() {
        try {
            if (db.isOpen()) {
                db.close();
            }
        }catch (Exception e){

        }

    }



    /*
     * open database
     */
    public void open() throws SQLiteException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.v("open database Exception", "error==" + e.getMessage());
            db = dbHelper.getReadableDatabase();
        }
    }




    public ArrayList<Track> getAllTracks(){
        ArrayList<Track> tracks =new ArrayList<Track>();

        String query = "SELECT  * FROM " + Constants.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);


        Track task=null;
        try {
            if (cursor.moveToFirst()) {
                do {
                    task = new Track();
                    task.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                    task.setTitle(cursor.getString(cursor.getColumnIndex(Constants.KEY_TITLE)));
                    task.setDate(cursor.getString(cursor.getColumnIndex(Constants.KEY_DATE)));
                    task.setSpeed(cursor.getString(cursor.getColumnIndex(Constants.KEY_SPEED)));
                    task.setTime(cursor.getString(cursor.getColumnIndex(Constants.KEY_TIME)));
                    task.setDistance(cursor.getString(cursor.getColumnIndex(Constants.KEY_DISTANCE)));
                    task.setPath(cursor.getString(cursor.getColumnIndex(Constants.KEY_PATH)));
                    tracks.add(task);
                } while (cursor.moveToNext());
            }
        }finally {
            cursor.close();
        }




        return tracks;

    }




    public long addTrack(Track track) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Constants.KEY_TITLE,track.getTitle());
        initialValues.put(Constants.KEY_DISTANCE, track.getDistance());
        initialValues.put(Constants.KEY_DATE,track.getDate());
        initialValues.put(Constants.KEY_SPEED,track.getSpeed());
        initialValues.put(Constants.KEY_TIME,track.getTime());
        initialValues.put(Constants.KEY_PATH,track.getPath());

        return db.insert(Constants.TABLE_NAME , null, initialValues);
    }



    public int deleteTrack(Track track){

        return db.delete(Constants.TABLE_NAME,Constants.KEY_ID +"=?",new String []{String.valueOf(track.getId())});
    }



}
