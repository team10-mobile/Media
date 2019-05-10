package com.example.mediaplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class AlbumsActivity extends AppCompatActivity {

    private ListView mListAlbums;

    private void mapping()
    {
        mListAlbums = findViewById(R.id.list_albums);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        mapping();
    }
}
