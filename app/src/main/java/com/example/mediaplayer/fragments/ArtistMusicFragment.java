package com.example.mediaplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.ArtistSongAdapter;
import com.example.mediaplayer.dataloader.ArtistSongLoader;
import com.example.mediaplayer.models.Song;

import java.util.ArrayList;


/**
 * Đây Fragment nằm bên trong fragment detail artist, nơi hiển thị tất cả bài hát và album mà nghệ
 * sĩ có
 */
public class ArtistMusicFragment extends Fragment {

    public static RecyclerView songsRecyclerview;

    private long artistID = -1;

    private ArtistSongAdapter mSongAdapter;

    public static ArtistMusicFragment newInstance(long id) {
        ArtistMusicFragment fragment = new ArtistMusicFragment();
        Bundle args = new Bundle();
        args.putLong("artist_id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong("artist_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_music, container, false);
        songsRecyclerview = view.findViewById(R.id.recycler_view_songs);
        setUpSongs();
        return view;
    }


    private void setUpSongs() {
        songsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<Song> songList;
        songList = ArtistSongLoader.getSongsForArtist(getActivity(), artistID);

        // adding one dummy song to top of arraylist
        //there will be albums header at this position in recyclerview
        songList.add(0, new Song(-1, -1, -1, "dummy", "dummy", "dummy", -1, -1));

        mSongAdapter = new ArtistSongAdapter((AppCompatActivity)getActivity(), songList, artistID);
        songsRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        songsRecyclerview.setAdapter(mSongAdapter);
    }

}
