package com.example.vimadhavan.mapmyroute.utils;

import android.os.Handler;

/**
 * Created by vimadhavan on 5/18/2017.
 */

public class Util {
    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }
}
