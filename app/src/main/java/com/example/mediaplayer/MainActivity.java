package com.example.mediaplayer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment();
    }
    // open fragment
    private void openFragment()
    {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.frame_music_control,new ControlMusicFragment());
        mFragmentTransaction.replace(R.id.frame_music_contains,new MyMusicFragment());
        mFragmentTransaction.replace(R.id.frame_music_mode,new ModeMusicFragment());
        mFragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.frame_music_contains);

        if(fragment != null)
        {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.remove(fragment);
            mFragmentTransaction.add(R.id.frame_music_contains,new MyMusicFragment());
            mFragmentTransaction.commit();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // TO DO:     click scan songs in local
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.button_scan_songs:
                // code scan song here
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
