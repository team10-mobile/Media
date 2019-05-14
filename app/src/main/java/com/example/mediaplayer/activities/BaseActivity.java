package com.example.mediaplayer.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.models.Audio;
import com.example.mediaplayer.service.MediaPlayerService;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.service.MusicStateListener;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity
        implements ServiceConnection, MusicStateListener {

    private ArrayList<MusicStateListener> musicStateListenerArrayList = new ArrayList<>();

    private MediaPlayerService mediaPlayerService;

    private boolean serviceBound = false;

    private BroadcastListener broadcastListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeService();
        registerBroadcast();
    }

    private void initializeService(){
        //Gọi dịch vụ tại đây
        MusicPlayer.bindToService(this,this);
    }

    /**
     * Phương thức đăng kí nhận thông báo từ dịch vụ để xử lý
     */
    private void registerBroadcast(){
        broadcastListener = new BroadcastListener();
        IntentFilter intentFilter = new IntentFilter("metaChanged.Broadcast");
       registerReceiver(broadcastListener,intentFilter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        //Xuat hien loi kho mo
       // unregisterReceiver(broadcastListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
           unbindService(this);
            //service is active
            mediaPlayerService.stopSelf();
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
        mediaPlayerService = binder.getService();
        serviceBound = true;
        ((MusicPlayer.ServiceBinder)service).onServiceConnected(name,service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceBound = false;
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        for (final MusicStateListener listener : musicStateListenerArrayList) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    public void setMusicStateListener(MusicStateListener stateListener){
        if(stateListener == this) return;
        if(stateListener != null){
            musicStateListenerArrayList.add(stateListener);
        }
    }

    //Lớp broadcast để đăng kí xử lý thông báo từ dịch vụ
    public class BroadcastListener extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            onMetaChanged();
        }
    }
}
