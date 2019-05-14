package com.example.mediaplayer.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.MediaStore;

import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.utils.MusicUtils;

import java.util.ArrayList;

public class MusicPlayer {

    private static Context mContext = null;

    private static ServiceBinder serviceBinder = null;

    private static MediaPlayerService mediaService = null;

    private static Intent intentService = null;

    private static boolean serviceBound = false;

    //Khi gọi phương thức này, thì base activity sẽ khởi động dịch vụ ràng buộc và không ràng buộc
    //Nó dữ lại intentService để các lớp khác sử dụng, tạo một ServiceBinder có khai triển
    //ServiceConnection và gọi liên kết dịch vụ, sau đó truyền cái ServiceBinder này vào làm tham
    //số của service connection, để rồi sau để từ Service Connection ở Base activity có thể ép
    //kiểu tham so Binder thành ServiceBinder và gọi các phương thức của lớp này để lấy ra
    //dịch vụ
    public static void bindToService(Context context, ServiceConnection serviceConnection) {
       if(mContext == null) mContext = context;
       if(intentService == null) {
           intentService = new Intent(context, MediaPlayerService.class);
           context.startService(intentService);
           ServiceBinder binder = new ServiceBinder(context, serviceConnection);
           serviceBinder = binder;
           context.bindService(intentService, binder, Context.BIND_AUTO_CREATE);
       }
    }

    //Kế thừa ServiceConnection
    public static class ServiceBinder implements ServiceConnection {

        private ServiceConnection mCallback;

        private Context mContext;

        public ServiceBinder(Context context, ServiceConnection callback) {
            mCallback = callback;
            mContext = context;
        }

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mediaService =((MediaPlayerService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {

        }
    }

    public static void playAll(Context context, int position,
                               MusicUtils.IdType sourceType, Song song, boolean shuffleMode){

        if(mediaService == null) return;

        int currentPosition = mediaService.getSongPositionCurrent();

        long currentId = mediaService.getSongId();

        if(currentPosition == position && currentId == song.id){
            if(mediaService.isPlaying()) {
                return;
            }
        }else{
            mediaService.resetMedia();
        }
        mediaService.setSongPositionCurrent(position);

        mediaService.setMediaFile(song);

        mediaService.initMediaPlayer();

        mediaService.playMedia(context);

        mediaService.senBroadcastToUpdateMusicState();
    }

    public  static String getFileSong(Context context, int position, MusicUtils.IdType sourceType) {
        Cursor cursor = SongLoader.getCursorData(context);
        int col = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToPosition(position);
        String fileName = cursor.getString(col);
        return fileName;
    }

    public static long getCurrentSongId(){
        if(mediaService != null) return  mediaService.getSongId();
        return  -1;
    }

    public static boolean isPlaying(){
        if(mediaService != null) return mediaService.isPlaying();
        return false;
    }

    public static Song getSongCurrent(){
        if(mediaService != null) return mediaService.getSongCurrent();
        return null;
    }

    public static void stop(){
        if(mContext != null) {
            if(intentService!=null) {
                mediaService.stopMedia();
            }
        }
    }


    public  static void setSongs(ArrayList<Song> songs){
        if(mediaService != null) mediaService.setSongs(songs);
    }

    public static void setDefaultSong(){
        if(mediaService != null) mediaService.setDefaultSong();
    }

    public static void playOrPause(){
        if(mediaService != null){
            if(mediaService.isPlaying()) {
                mediaService.pauseMedia();
            }else{
                mediaService.playMedia(mContext);
                mediaService.senBroadcastToUpdateMusicState();
            }
        }
    }
}
