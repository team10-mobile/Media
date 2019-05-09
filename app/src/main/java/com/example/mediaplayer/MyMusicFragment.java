package com.example.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class MyMusicFragment extends Fragment {

    private LinearLayout mSongs, mPlaylist, mDownload, mFavorites, mArtist, mAlbums;
    private View mView;
    private Intent mIntent;
    private void mapping()
    {
        mSongs = mView.findViewById(R.id.songs);
        mPlaylist = mView.findViewById(R.id.playlist);
        mDownload = mView.findViewById(R.id.download);
        mFavorites = mView.findViewById(R.id.favorites);
        mArtist = mView.findViewById(R.id.artists);
        mAlbums = mView.findViewById(R.id.albums);
    }
    // To do open
    private void openLibrary()
    {
        mSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(),SongsActivity.class);
                startActivity(mIntent);
            }
        });
        mPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(),PlaylistActivity.class);
                startActivity(mIntent);
            }
        });
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(),DownLoadActivity.class);
                startActivity(mIntent);
            }
        });
        mFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(),FavoritesActivity.class);
                startActivity(mIntent);
            }
        });
        mArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(),ArtistsActivity.class);
                startActivity(mIntent);
            }
        });
        mAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(getActivity(),AlbumsActivity.class);
                startActivity(mIntent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_music, container, false);
        mapping();
        openLibrary();
        // Inflate the layout for this fragment
        return mView;
    }



}
