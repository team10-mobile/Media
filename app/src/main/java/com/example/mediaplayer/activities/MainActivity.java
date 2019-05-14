package com.example.mediaplayer.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mediaplayer.fragments.ControlMusicFragment;
import com.example.mediaplayer.fragments.ModeMusicFragment;
import com.example.mediaplayer.fragments.MyMusicFragment;
import com.example.mediaplayer.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends BaseActivity {

    private FragmentManager mFragmentManager;

    private FragmentTransaction mFragmentTransaction;

    //themzo
    ImageButton like, notlike,dislike,notdislike;
    ImageButton play,pause,play_main,pause_main;
    private SlidingUpPanelLayout mLayout;
    //endthemzo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment();
        //themzo
        like = findViewById(R.id.imageButton2);
        notlike =  findViewById(R.id.imageButton2new);
        dislike = findViewById(R.id.button);
        notdislike =  findViewById(R.id.buttontwo);
        play = findViewById(R.id.play_button);
        pause = findViewById(R.id.pause_button);
        play_main =  findViewById(R.id.play_button_main);
        pause_main =  findViewById(R.id.pause_button_main);
        mLayout =  findViewById(R.id.frame_music_contains_2);
        themzo();
        //endthemzo
    }
    // open fragment
    private void openFragment()
    {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ///Them music control tai day
       // mFragmentTransaction.replace(R.id.frame_music_control,new ControlMusicFragment());

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

        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                        || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
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

    private void themzo(){
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notlike.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"You Like the Song",Toast.LENGTH_SHORT).show();
                if (notdislike.getVisibility() == View.VISIBLE){
                    notdislike.setVisibility(View.GONE);
                }
            }
        });

        notlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notlike.setVisibility(View.GONE);
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notdislike.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"You DisLike the Song",Toast.LENGTH_SHORT).show();
                if (notlike.getVisibility() == View.VISIBLE){
                    notlike.setVisibility(View.GONE);
                }
            }
        });

        notdislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notdislike.setVisibility(View.GONE);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"Song Is now Playing",Toast.LENGTH_SHORT).show();
                if (play_main.getVisibility() == View.VISIBLE){
                    play_main.setVisibility(View.GONE);
                    pause_main.setVisibility(View.VISIBLE);
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"Song is Pause",Toast.LENGTH_SHORT).show();
                if (pause_main.getVisibility() == View.VISIBLE){
                    pause_main.setVisibility(View.GONE);
                    play_main.setVisibility(View.VISIBLE);
                }
            }
        });

        play_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_main.setVisibility(View.GONE);
                pause_main.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"Song Is now Playing",Toast.LENGTH_SHORT).show();
                if (play.getVisibility() == View.VISIBLE){
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
            }
        });

        pause_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause_main.setVisibility(View.GONE);
                play_main.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"Song is Pause",Toast.LENGTH_SHORT).show();
                if (pause.getVisibility() == View.VISIBLE){
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}
