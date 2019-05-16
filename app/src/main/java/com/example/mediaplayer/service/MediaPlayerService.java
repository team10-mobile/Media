package com.example.mediaplayer.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.mediaplayer.models.Artist;
import com.example.mediaplayer.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MediaPlayerService extends Service implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    public static final String UPDATE_PREFERENCES = "updatepreferences";

    public static final String MEDIA_IS_PLAYING = "MEDIA_IS_PLAYING";

    public static final String MEDIA_IS_STOP = "MEDIA_IS_STOP";

    public static final int SHUFFLE_NONE = 0;

    public static final int SHUFFLE_NORMAL = 1;

    public static final int SHUFFLE_AUTO = 2;

    public static final int REPEAT_NONE = 0;

    public static final int REPEAT_CURRENT = 1;

    public static final int REPEAT_ALL = 2;

    private int mShuffleMode = SHUFFLE_NONE;

    private int mRepeatMode = REPEAT_NONE;

    private MediaPlayer mediaPlayer;

    private final IBinder binder = new LocalBinder();

    // Được sử dụng để tạm dừng / tiếp tục MediaPlayer
    private int resumePosition;

    private int currentPosOfSong = 0;

    private Song songCurrent;

    private ArrayList<Song> listAllSongs = null;

    private boolean mIsInitialized = false;

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    public void setArtists(List<Artist> artists) {
    }

    public void refresh() {
        senBroadcastToUpdateMusicState();
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // Được gọi biểu thị trạng thái bộ đệm của một tài nguyên truyền thông
        // đang được truyền phát qua mạng.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Được gọi khi phát lại nguồn phương tiện đã hoàn thành.
        if(mShuffleMode == SHUFFLE_NORMAL){
            playRandom();
        }
        else if(mRepeatMode == REPEAT_NONE){
            stopMedia();
            resetMedia();
            senBroadcastToUpdateMusicState();
        }else if(mRepeatMode == REPEAT_CURRENT){
            playCurrent();
        }else if(mRepeatMode == REPEAT_ALL){
            next();
        }

    }

    private void playCurrent(){
        resetMedia();
        setDataForMedia();
        mediaPlayer.prepareAsync();
    }

    private void playRandom(){
        if(listAllSongs==null)return;
        if(listAllSongs.size()<=0) return;
        Random  rd = new Random();
        int pos = rd.nextInt(listAllSongs.size());
        currentPosOfSong = pos;
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // Được gọi khi có lỗi trong hoạt động không đồng bộ.

        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        // Được gọi để truyền đạt một số thông tin.
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // Được gọi khi nguồn phương tiện đã sẵn sàng để phát lại.
        play();
        senBroadcastToUpdateMusicState();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

        // Được gọi chỉ ra việc hoàn thành một hoạt động tìm kiếm.
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            return false;
        }
        return super.onUnbind(intent);
    }

    /**
     * Hàm khởi tạo và đăng kí sự kiện cho media player
     */
    public void initMediaPlayer() {
        if (mediaPlayer != null) return;
        mediaPlayer = new MediaPlayer();
        // Thiết lập trình nghe sự kiện MediaPlayer
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mIsInitialized = true;
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    //Lớp play bài hát, nếu media chưa được khởi tạo thì sẽ thoát khỏi hàm, nếu bài hát có
    // một bài hát đang play và người dùng muốn mở bài mới thì dịch vụ sẽ gọi hàm #1, ngược lại
    //kiểm tra nếu bài hát đang pause thì tiếp tục bài hát.
    public void playMedia(Context context) {

        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        if (isPausing) {
            mediaPlayer.start();
            isPausing = false;
            return;
        }

        if(!setDataForMedia()){
            Toast.makeText(context,"File not found, playing next !",Toast.LENGTH_SHORT).show();
            return;
        }
        mediaPlayer.prepareAsync();
    }

    public boolean setDataForMedia() {
        long currSong = songCurrent.id;
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
            return true;
        } catch (Exception e) {
            try {
                mediaPlayer.setDataSource(String.valueOf(songCurrent.uri));
                mediaPlayer.setOnPreparedListener(this);
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e("MUSIC SERVICE", "Error setting data source", e);
                return false;
            }
        }
    }

    public void resetMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPausing = false;
        }
    }

    public void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            isPausing = false;
        }
    }

    private boolean isPausing = false;

    public void pauseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPausing = true;
            resumePosition = mediaPlayer.getCurrentPosition();
            senBroadcastToUpdateMusicState();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
    }

    //Set media file cho MediaPlaer
    public void setMediaFile(Song song) {
        this.songCurrent = song;
    }

    //Trả về bài hát đang được phát hiện tại
    public Song getSongCurrent() {
        return songCurrent;
    }

    public void setSongPositionCurrent(int positionCurrent) {
        this.currentPosOfSong = positionCurrent;
    }

    public int getSongPositionCurrent() {
        return currentPosOfSong;
    }

    public long getSongId() {
        if (songCurrent != null) return songCurrent.id;
        return -1;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) return mediaPlayer.isPlaying();
        return false;
    }

    private void play() {
        if(mediaPlayer!=null)
        mediaPlayer.start();
    }

    public void setSongs(ArrayList<Song> songs) {
        this.listAllSongs = songs;
    }

    public void setDefaultSong() {
        if (listAllSongs != null) {
            songCurrent = listAllSongs.get(currentPosOfSong);
        }
    }

    public void senBroadcastToUpdateMusicState() {
        Intent intent = new Intent();
        intent.setAction("metaChanged.Broadcast");
        sendBroadcast(intent);
    }

    public long duration() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        return 0;
    }

    public long seek(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
            return position;
        }
        return 0;
    }

    public long seek(long position) {
        if (position < 0) {
            position = 0;
        } else if (position > mediaPlayer.getDuration()) {
            position = mediaPlayer.getDuration();
        }
        long result = seek(position);
        //notifyChange(POSITION_CHANGED);
        return result;
    }

    public void next() {
        if (listAllSongs == null )return;
        if(listAllSongs.size() <= 0) return;
        int pos = currentPosOfSong;
        pos++;
        if (pos > listAllSongs.size() - 1) {
            pos = 0;
        }
       updatePosition(pos);
    }

    public void previous() {
        if (listAllSongs == null )return;
        if(listAllSongs.size() <= 0) return;
        int pos = currentPosOfSong;
        pos--;
        if (pos < 0) {
            pos = listAllSongs.size() - 1;
        }
       updatePosition(pos);
    }

    private void updatePosition(int pos){
        currentPosOfSong = pos;
        songCurrent = listAllSongs.get(pos);

        resetMedia();
        if(!setDataForMedia())return;
        mediaPlayer.prepareAsync();
        senBroadcastToUpdateMusicState();
    }

    public int getRepeatMode() {
        return mRepeatMode;
    }

    public void setRepeatMode(int repeatmode) {
        synchronized (this) {
            mRepeatMode = repeatmode;
        }
    }

    public int getShuffleMode() {
        return mShuffleMode;
    }

    public void setmShuffleMode(int shuffleMode) {
        synchronized (this) {
            mShuffleMode = shuffleMode;
        }
    }


}
