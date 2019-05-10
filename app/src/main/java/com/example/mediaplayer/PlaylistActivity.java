package com.example.mediaplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

public class PlaylistActivity extends AppCompatActivity {

    private ImageButton mAddNamePlaylist;
    private ListView mListPlaylist;

    // mapping
    private void mapping()
    {
        mAddNamePlaylist = findViewById(R.id.add_name_playlist);
        mListPlaylist = findViewById(R.id.list_playlist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        mapping();
        addNamePlaylist();
    }
    // add name playlist
    private void addNamePlaylist()
    {
        mAddNamePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: add name playlist
            }
        });
    }
}
