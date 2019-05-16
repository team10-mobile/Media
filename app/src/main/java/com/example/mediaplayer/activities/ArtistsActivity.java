package com.example.mediaplayer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.mediaplayer.R;

public class ArtistsActivity extends AppCompatActivity {

    private ListView mListArtist;

    private void mapping() {
        mListArtist = findViewById(R.id.list_artists);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);
        mapping();
    }
}
