package com.kevinberg.almacenpago;

import static java.lang.Integer.parseInt;

import android.app.Service;

import android.content.Intent;

import android.media.MediaPlayer;
import android.os.IBinder;


public class BackgroundSoundService extends Service {
    private static final String TAG = null;

    MediaPlayer player;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        player = MediaPlayer.create(this, R.raw.allstar);
        player.setLooping(true);
        player.setVolume(100,100);
    }

    public int onStartCommand(Intent intent, int flags, int startID){
        player.start();
        return Service.START_STICKY;
    }

    public IBinder onUnBind(Intent arg0){
        player.stop();
        return null;
    }

    public void onPause(){
        player.pause();
    }


    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }
}
