package com.example.mediaplayer.activities;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mediaplayer.R;

public class PlaySongsActivity extends AppCompatActivity {

    private ImageButton mModelPlay, mPlayStop, mFavorites;
    private Boolean mIsPlay = false;
    private Boolean mIsFavorites = false;
    private Models mModel = Models.normal;
    private Animation mRotateDisk;
    private ImageButton mTimeOff;
    private ImageView mDisk,mComeBack;
    private ImageButton mAddPlaylist, mPreviousSong, mNextSong;

    private enum Models{ shuffle, normal, repeat }

    // mapping views
    private void mapping()
    {
        mModelPlay = findViewById(R.id.model_play);
        mPlayStop = findViewById(R.id.play_stop);
        mFavorites = findViewById(R.id.favorites);
        mDisk = findViewById(R.id.disk);
        mTimeOff = findViewById(R.id.time_off);
        mAddPlaylist = findViewById(R.id.choose_playlist);
        mComeBack = findViewById(R.id.comeback);
        mPreviousSong = findViewById(R.id.previous_song);
        mNextSong = findViewById(R.id.next_song);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_songs);
        mRotateDisk = AnimationUtils.loadAnimation(this,R.anim.disk_rotate);
        mapping();
        clickChangeButton();
    }

    // handle: play, stop, previous, next, mode songs
    private void clickChangeButton()
    {
        mPreviousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: handle when user click previous song
            }
        });
        mNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: handle when user click next song
            }
        });
        mPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsPlay)
                {
                    mPlayStop.setImageResource(R.drawable.ic_stop);
                    mIsPlay = false;
                    mDisk.clearAnimation();
                    // todo: handle when user stop music
                }
                else{
                    mPlayStop.setImageResource(R.drawable.ic_play);
                    mIsPlay = true;
                    mDisk.startAnimation(mRotateDisk);
                    // todo: handle when user play music
                }
            }
        });
        mModelPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mModel)
                {
                    case normal:
                        mModelPlay.setImageResource(R.drawable.ic_repeat);
                        mModel = Models.repeat;
                        Toast.makeText(PlaySongsActivity.this,"Repeat",Toast.LENGTH_SHORT).show();
                        // todo: handle when mode repeat
                        break;
                    case repeat:
                        mModelPlay.setImageResource(R.drawable.ic_shuffle);
                        mModel = Models.shuffle;
                        Toast.makeText(PlaySongsActivity.this,"shuffle",Toast.LENGTH_SHORT).show();
                        // todo: handle when mode shuffle
                        break;
                    case shuffle:
                        mModelPlay.setImageResource(R.drawable.ic_nomal);
                        mModel = Models.normal;
                        Toast.makeText(PlaySongsActivity.this,"normal",Toast.LENGTH_SHORT).show();
                        // todo: handle when mode normal
                        break;
                }
            }
        });
        mFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsFavorites)
                {
                    mFavorites.setImageResource(R.drawable.ic_not_love);
                    mIsFavorites = false;
                    // todo: handle when user choose there songs is favorites
                }
                else
                {
                    mFavorites.setImageResource(R.drawable.ic_love);
                    mIsFavorites = true;
                    // todo: handle when user choose there songs is not favorites
                }
            }
        });
        mTimeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PlaySongsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.time_off_dialog);
                dialog.show();
                // todo: handle time off songs
            }
        });
        mAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PlaySongsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_playlist_dialog);
                dialog.show();
                // todo: handle add song into playlist
            }
        });
        // comeback
        mComeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
