package com.kevinberg.almacenpago;

import static java.lang.Integer.parseInt;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;

    /*
    private BroadcastReceiver StateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            if (parseInt(String.valueOf(status)) == 0) {
                player.pause();
            } else if (parseInt(String.valueOf(status)) == 1) {
                if (player == null) {
                    player = MediaPlayer.create(context, R.raw.allstar);
                    player.setLooping(true);
                }
                player.start();
            } else if (player != null && player.isPlaying()) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }
        }
    };
     */

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        //LocalBroadcastManager.getInstance(this).registerReceiver(StateReceiver, new IntentFilter("status"));

        player = MediaPlayer.create(this, R.raw.allstar);
        player.setLooping(true);
    }

    public int onStartCommand(Intent intent, int flags, int startID){
        player.start();
        return Service.START_NOT_STICKY;
    }

    public IBinder onUnBind(Intent arg0){
        return null;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        stopSelf();
        super.onDestroy();
    }
}
