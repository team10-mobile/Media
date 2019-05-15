package com.example.mediaplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activities.DownLoadActivity;
import com.example.mediaplayer.activities.FavoritesActivity;
import com.example.mediaplayer.activities.PlaylistActivity;


public class MyMusicFragment extends Fragment {

    private LinearLayout mSongs, mPlaylist, mDownload, mFavorites, mArtist, mAlbums;
    private View mView;
    private Intent mIntent;

    // mapping views
    private void mapping()
    {
        mSongs = mView.findViewById(R.id.songs);
        mPlaylist = mView.findViewById(R.id.playlist);
        mDownload = mView.findViewById(R.id.download);
        mFavorites = mView.findViewById(R.id.favorites);
        mArtist = mView.findViewById(R.id.artists);
        mAlbums = mView.findViewById(R.id.albums);
    }
    // open activities
    private void openLibrary()
    {
        mSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new SongsFragment();
                FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_music_contains,fragment).commitAllowingStateLoss();
            }
        });

        mPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mIntent = new Intent(getActivity(), PlaylistActivity.class);
                startActivity(mIntent);*/
                Toast.makeText(getActivity(),"PLAY LIST",Toast.LENGTH_SHORT).show();
                Fragment fragment = new PlaylistFragment();
                FragmentTransaction transaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                //transaction.hide(getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_music_contains));
                transaction.replace(R.id.frame_music_contains, fragment).commitAllowingStateLoss();
            }
        });
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(), DownLoadActivity.class);
                startActivity(mIntent);
            }
        });
        mFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(), FavoritesActivity.class);
                startActivity(mIntent);
            }
        });
        mArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ArtistFragment();
                FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_music_contains,fragment).commitAllowingStateLoss();
            }
        });
        mAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AlbumFragment();
                FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_music_contains,fragment).commitAllowingStateLoss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_music, container, false);
        mapping();
        openLibrary();
        return mView;
    }



}
