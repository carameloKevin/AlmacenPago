package com.kevinberg.almacenpago;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.background);
        player.setLooping(true);
        player.setVolume(100,100);
    }

    public int onStartCommand(Intent intent, int flags, int startID){
        player.setLooping(true);
        player.start();
        return Service.START_STICKY;
    }

    public IBinder onUnBind(Intent arg0){
        player.stop();
        return null;
    }

    public void onPause(){
        player.stop();
    }


    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }
}
