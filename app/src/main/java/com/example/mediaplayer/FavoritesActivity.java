package com.example.mediaplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class FavoritesActivity extends AppCompatActivity {

    private ListView mListSongFavorites;

    private void mapping()
    {
        mListSongFavorites  = findViewById(R.id.list_favorites);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        mapping();
    }
}
