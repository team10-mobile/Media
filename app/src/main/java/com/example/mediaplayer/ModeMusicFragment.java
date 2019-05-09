package com.example.mediaplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ModeMusicFragment extends Fragment {

    private View mView;
    private ImageView mLocalMusic;
    private ImageView mOnlineMusic;
    private FragmentManager mFragmentManager;
    private MyMusicFragment mMyMusicFragment;
    private OnlineMusicFragment mOnlineMusicFragment;


    private void mapping()
    {
        mLocalMusic = mView.findViewById(R.id.local_music);
        mOnlineMusic = mView.findViewById(R.id.online_music);
    }

    private void changeModelMusic()
    {
        mLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_music_contains,mMyMusicFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mOnlineMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_music_contains,mOnlineMusicFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mode_music,container,false);
        mMyMusicFragment = new MyMusicFragment();
        mOnlineMusicFragment = new OnlineMusicFragment();
        mapping();
        mFragmentManager = getFragmentManager();
        changeModelMusic();
        return mView;
    }
}
