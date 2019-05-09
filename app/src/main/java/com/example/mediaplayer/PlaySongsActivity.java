package com.example.mediaplayer;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PlaySongsActivity extends AppCompatActivity {

    private ImageButton mModelPlay, mPlayStop, mFavorites;
    private Boolean mIsPlay = false;
    private Boolean mIsFavorites = false;
    private Models mModel = Models.nomal;
    private Animation mRotateDisk;
    private ImageButton mTimeOff;
    private ImageView mDisk;
    private ImageButton mAddPlaylist;

    private enum Models{ shuffle, nomal, repeat }

    private void mapping()
    {
        mModelPlay = findViewById(R.id.model_play);
        mPlayStop = findViewById(R.id.play_stop);
        mFavorites = findViewById(R.id.favorites);
        mDisk = findViewById(R.id.disk);
        mTimeOff = findViewById(R.id.time_off);
        mAddPlaylist = findViewById(R.id.choose_playlist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_songs);
        mRotateDisk = AnimationUtils.loadAnimation(this,R.anim.disk_rotate);
        mapping();
        clickChangeButton();
    }

    private void clickChangeButton()
    {
        mPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsPlay)
                {
                    mPlayStop.setImageResource(R.drawable.ic_stop);
                    mIsPlay = false;
                    mDisk.clearAnimation();
                }
                else{
                    mPlayStop.setImageResource(R.drawable.ic_play);
                    mIsPlay = true;
                    mDisk.startAnimation(mRotateDisk);
                }
            }
        });
        mModelPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mModel)
                {
                    case nomal:
                        mModelPlay.setImageResource(R.drawable.ic_repeat);
                        mModel = Models.repeat;
                        Toast.makeText(PlaySongsActivity.this,"Repeat",Toast.LENGTH_SHORT).show();
                        break;
                    case repeat:
                        mModelPlay.setImageResource(R.drawable.ic_shuffle);
                        mModel = Models.shuffle;
                        Toast.makeText(PlaySongsActivity.this,"shuffle",Toast.LENGTH_SHORT).show();
                        break;
                    case shuffle:
                        mModelPlay.setImageResource(R.drawable.ic_nomal);
                        mModel = Models.nomal;
                        Toast.makeText(PlaySongsActivity.this,"nomal",Toast.LENGTH_SHORT).show();
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
                }
                else
                {
                    mFavorites.setImageResource(R.drawable.ic_love);
                    mIsFavorites = true;
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
            }
        });
        mAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PlaySongsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_playlist_dialog);
                dialog.show();
            }
        });
    }
}
