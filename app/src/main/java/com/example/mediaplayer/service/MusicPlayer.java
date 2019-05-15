package com.example.mediaplayer.service;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dataloader.ArtistLoader;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.models.Artist;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.utils.MusicUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {

    private static Context mContext = null;

    private static ServiceBinder serviceBinder = null;

    private static MediaPlayerService mediaService = null;

    private static Intent intentService = null;

    private static boolean serviceBound = false;

    private static ContentValues[] mContentValuesCache = null;

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

            setSongs(SongLoader.getAllSongs(mContext));

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

    public  static void setArtists(List<Artist> artists){
        if(mediaService != null) mediaService.setArtists(artists);
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

    public static long duration(){
        if(mediaService!=null) return mediaService.duration();
        return 0;
    }

    public static void seek(int position){
        if(mediaService!=null)mediaService.seek(position);
    }

    public static void next(){
        if(mediaService!=null)mediaService.next();
    }

    public static void previous(){
        if(mediaService!=null)mediaService.previous();
    }

    public static void cycleRepeat(){
        if(mediaService!=null){
            switch (mediaService.getRepeatMode()){
                case MediaPlayerService.REPEAT_NONE:
                    mediaService.setRepeatMode(MediaPlayerService.REPEAT_CURRENT);
                    break;

                case  MediaPlayerService.REPEAT_CURRENT:
                    mediaService.setRepeatMode(MediaPlayerService.REPEAT_ALL);
                    break;

                case MediaPlayerService.REPEAT_ALL:
                    mediaService.setRepeatMode(MediaPlayerService.REPEAT_NONE);
                    if(mediaService.getShuffleMode() != MediaPlayerService.SHUFFLE_NONE){
                        mediaService.setmShuffleMode(MediaPlayerService.SHUFFLE_NONE);
                    }
                    break;

                 default:mediaService.setRepeatMode(MediaPlayerService.REPEAT_NONE);break;

            }
        }
    }

    public static void cycleShuffle(){
        if(mediaService!=null){
            switch (mediaService.getShuffleMode()){
                case MediaPlayerService.SHUFFLE_NONE:
                    mediaService.setmShuffleMode(MediaPlayerService.SHUFFLE_NORMAL);
                    break;
                    case MediaPlayerService.SHUFFLE_NORMAL:
                        mediaService.setmShuffleMode(MediaPlayerService.SHUFFLE_NONE);
                        break;
                        default:break;
            }
        }
    }

    public static final long createPlaylist(final Context context, final String name) {
        if (name != null && name.length() > 0) {
            final ContentResolver resolver = context.getContentResolver();
            final String[] projection = new String[]{
                    MediaStore.Audio.PlaylistsColumns.NAME
            };
            final String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";
            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    projection, selection, null, null);
            if (cursor.getCount() <= 0) {
                final ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                final Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        values);
                return Long.parseLong(uri.getLastPathSegment());
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            return -1;
        }
        return -1;
    }

    public static void addToPlaylist(final Context context, final long[] ids, final long playlistid) {
        final int size = ids.length;
        final ContentResolver resolver = context.getContentResolver();
        final String[] projection = new String[]{
                "max(" + "play_order" + ")",
        };
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        Cursor cursor = null;
        int base = 0;

        try {
            cursor = resolver.query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                base = cursor.getInt(0) + 1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        int numinserted = 0;
        for (int offSet = 0; offSet < size; offSet += 1000) {
            makeInsertItems(ids, offSet, 1000, base);
            numinserted += resolver.bulkInsert(uri, mContentValuesCache);
        }
        final String message = context.getResources().getQuantityString(
                R.plurals.NNNtrackstoplaylist, numinserted, numinserted);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void makeInsertItems(final long[] ids, final int offset, int len, final int base) {
        if (offset + len > ids.length) {
            len = ids.length - offset;
        }

        if (mContentValuesCache == null || mContentValuesCache.length != len) {
            mContentValuesCache = new ContentValues[len];
        }
        for (int i = 0; i < len; i++) {
            if (mContentValuesCache[i] == null) {
                mContentValuesCache[i] = new ContentValues();
            }
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids[offset + i]);
        }
    }

    public static final boolean isPlaybackServiceConnected() {
        return mediaService != null;
    }

    public static void refresh() {
            if (mediaService != null) {
                mediaService.refresh();
            }
        }

}
