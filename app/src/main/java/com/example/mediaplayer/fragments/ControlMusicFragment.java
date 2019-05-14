package com.example.mediaplayer.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activities.BaseActivity;
import com.example.mediaplayer.activities.PlaySongsActivity;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MediaPlayerService;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.service.MusicStateListener;

//Fragment hiển thị bài nhạc và mở một fragment mới để chơi nhạc, khai triển musicstatelistener
//để có thể update giao diện khi trạng thái nhạc thay đổi
public class ControlMusicFragment extends Fragment implements MusicStateListener {

    private View mView;

    private RelativeLayout mControl;

    private ImageView mPlayPause;

    private Intent mIntent;

    private Animation mTotaleDisk;

    private ImageView mDisk;

    private TextView txtNameSong;

    private TextView txtNameArtist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_control_music, container, false);
        mTotaleDisk = AnimationUtils.loadAnimation(getContext(), R.anim.disk_rotate);
        mapping();
        openPlaySong();
        controlSongs();
        setDefaultSongs();
        ((BaseActivity) getActivity()).setMusicStateListener(this);
        return mView;
    }

    private void controlSongs() {
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayer.isPlaying()) {
                    mPlayPause.setImageResource(R.drawable.ic_play);
                    mDisk.clearAnimation();
                } else {
                    mPlayPause.setImageResource(R.drawable.ic_stop);
                    mDisk.startAnimation(mTotaleDisk);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                    }
                }, 200);
            }
        });
    }

    private void openPlaySong() {
        mControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(), PlaySongsActivity.class);
                startActivity(mIntent);
            }
        });
    }

    private void mapping() {
        txtNameSong = mView.findViewById(R.id.txt_name_song);
        txtNameArtist = mView.findViewById(R.id.txt_name_artist);
        mControl = mView.findViewById(R.id.layout_control);
        mPlayPause = mView.findViewById(R.id.button_stop_play);
        mDisk = mView.findViewById(R.id.disk);
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        if(MusicPlayer.isPlaying()) {
            mPlayPause.setImageResource(R.drawable.ic_stop);
            mDisk.startAnimation(mTotaleDisk);
            Song song = MusicPlayer.getSongCurrent();
            txtNameArtist.setText("   " + song.artistName);
            txtNameSong.setText("   " + song.title);
        }
    }

    private void setDefaultSongs(){
        //Khởi gán mặc định
        Song song = MusicPlayer.getSongCurrent();
        if (song != null) {
            txtNameSong.setText(song.title);
            txtNameArtist.setText(song.artistName);
        }
    }
}
