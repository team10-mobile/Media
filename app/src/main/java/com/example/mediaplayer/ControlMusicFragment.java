package com.example.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ControlMusicFragment extends Fragment {

    private View mView;
    private RelativeLayout mControl ;
    private ImageView mPlayPause;
    private Intent mIntent;
    private ImageView mNext, mPrevious;
    private Animation mTotaleDisk;
    private ImageView mDisk;

    private boolean mIsPlay = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_control_music,container,false);
        mTotaleDisk = AnimationUtils.loadAnimation(getContext(),R.anim.disk_rotate);
        mapping();
        openPlaySong();
        controlSongs();
        return mView;
    }

    private void controlSongs()
    {
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsPlay) {
                    mPlayPause.setImageResource(R.drawable.ic_play);
                    mIsPlay = true;
                    mDisk.startAnimation(mTotaleDisk);
                }
                else
                {
                    mPlayPause.setImageResource(R.drawable.ic_stop);
                    mIsPlay = false;
                    mDisk.clearAnimation();
                }
            }
        });
        // To do: next music
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        // To do: previous music
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void openPlaySong()
    {
        mControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(),PlaySongsActivity.class);
                startActivity(mIntent);
            }
        });
    }

    private void mapping()
    {
        mControl = mView.findViewById(R.id.layout_control);
        mPlayPause = mView.findViewById(R.id.button_stop_play);
        mNext = mView.findViewById(R.id.next);
        mPrevious = mView.findViewById(R.id.button_previous);
        mDisk = mView.findViewById(R.id.disk);
    }
}
