package com.example.vimadhavan.mapmyroute.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimadhavan.mapmyroute.R;
import com.example.vimadhavan.mapmyroute.activity.LoadRoute;
import com.example.vimadhavan.mapmyroute.activity.PopupActivity;
import com.example.vimadhavan.mapmyroute.activity.SavedRoutesActivity;
import com.example.vimadhavan.mapmyroute.database.DBhandler;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vimadhavan on 5/18/2017.
 */

public class TrackAdapter extends BaseAdapter{

    private SavedRoutesActivity activity;
    private ArrayList<Track> _data;

    public TrackAdapter(SavedRoutesActivity activity, ArrayList<Track> _data) {

        this._data = _data;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int position) {
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Track track = (Track) _data.get(position);
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.track_item, parent,false);

            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.titleTxt);
            holder.desx = (TextView) convertView.findViewById(R.id.descriptionTxt);

            holder.deleteImg= (ImageView) convertView.findViewById(R.id.deleteImg);
            holder.infoImg= (ImageView) convertView.findViewById(R.id.infoImg);

           // Log.d("DEbug:Vignesh IF",holder.title.getText().toString());
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "Delete Image:"+position, Toast.LENGTH_LONG).show();
                    Track selectedTrack = (Track) _data.get(position);
                    DBhandler.getInstance(activity).deleteTrack(selectedTrack);
                    activity.allTracks.remove(position);
                    if (activity.allTracks.isEmpty()){
                        activity.defaultText.setText(activity.getString(R.string.noTracks));

                        activity.defaultText.setVisibility(View.VISIBLE);
                        activity.allTrackList.setVisibility(View.GONE);
                    }
                    activity.allTaskAdapter.notifyDataSetChanged();
                    activity.allTrackList.invalidateViews();
                    notifyDataSetChanged();
                }
            });

            holder.infoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "Info Image:"+position, Toast.LENGTH_LONG).show();
                    Track selectedTrack = (Track) _data.get(position);
                    Intent loadRouteActivity =new Intent(activity,LoadRoute.class);

                    loadRouteActivity.putExtra("Distance",selectedTrack.getDistance());
                    loadRouteActivity.putExtra("Speed",selectedTrack.getSpeed());
                    loadRouteActivity.putExtra("Time",selectedTrack.getTime());
                    loadRouteActivity.putExtra("Path",selectedTrack.getPath());
                    loadRouteActivity.putExtra("Date",selectedTrack.getDate());
                    loadRouteActivity.putExtra("Title",selectedTrack.getTitle());

                    activity.startActivity(loadRouteActivity);
                }
            });


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

       // Log.d("DEbug:Vignesh",_data.get(position).getTitle());


        holder.title.setText(track.getTitle());
        String desc="Distance: "+track.getDistance()+"\nTime: "+track.getTime()+"\nDate: "+track.getDate();
        holder.desx.setText(desc);




        // Set image if exists

        return convertView;
    }


    static class ViewHolder {
        ImageView infoImg,deleteImg;
        TextView title, desx;

    }
}
