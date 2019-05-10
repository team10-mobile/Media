package com.example.mediaplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class DownLoadActivity extends AppCompatActivity {

    private ListView mListDowload;

    private void mapping()
    {
        mListDowload = findViewById(R.id.list_download);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        mapping();
    }
}
