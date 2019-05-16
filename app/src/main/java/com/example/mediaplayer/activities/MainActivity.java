package com.example.mediaplayer.activities;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mediaplayer.fragments.ModeMusicFragment;
import com.example.mediaplayer.fragments.MyMusicFragment;
import com.example.mediaplayer.R;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.utils.MusicUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;

public class MainActivity extends BaseActivity {

    private FragmentManager mFragmentManager;

    private FragmentTransaction mFragmentTransaction;

    private Animation mTotaleDisk;

    //add new
    private SlidingUpPanelLayout mLayout;

    private ImageButton play, pause, play_main, pause_main;

    private ImageView songArt;

    private ImageView circleImageSong;

    private TextView txtSongTitle, txtNameArtist;

    private TextView txtStartTime, txtEndTime;

    private SeekBar seekBarSong;

    private ImageButton btnPrevious, btnForward;

    private ImageButton btnRepeatAll, btnRepeatOne, btnRepeatOff, btnShuffleOn,btnShuffleOff;

    private int overFlowCounter = 0;

    private boolean fragmentPaused = false;

    //end add new

    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            //thoi luong dang duoc phat hien tai cua bai hat
            long position = MusicPlayer.duration();
            seekBarSong.setProgress((int) position);
            SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
            txtStartTime.setText(formatTime.format(position));
            overFlowCounter--;
            if (MusicPlayer.isPlaying()) {
                int delay = (int) (1500 - (position % 1000));
                if (overFlowCounter < 0 && !fragmentPaused) {
                    overFlowCounter++;
                    seekBarSong.postDelayed(mUpdateProgress, delay);
                }
            } else seekBarSong.removeCallbacks(this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment();
        //add new
        initializeUI();
        setEventForUI();
        //end add new
    }

    private void initializeUI() {
        mTotaleDisk = AnimationUtils.loadAnimation(this, R.anim.disk_rotate);
        btnForward = findViewById(R.id.btnForward);
        btnPrevious = findViewById(R.id.btnPrevious);

        btnRepeatOff = findViewById(R.id.btnRepeat);
        btnRepeatAll = findViewById(R.id.btnRepeatAll);
        btnRepeatOne = findViewById(R.id.btnRepeatOne);

        btnShuffleOff = findViewById(R.id.btnShuffleOff);
        btnShuffleOn = findViewById(R.id.btnShuffleOn);

        circleImageSong = findViewById(R.id.circleImageView);
        play = findViewById(R.id.play_button);
        pause = findViewById(R.id.pause_button);
        play_main = findViewById(R.id.play_button_main);
        pause_main = findViewById(R.id.pause_button_main);
        mLayout = findViewById(R.id.frame_music_contains_2);
        txtSongTitle = findViewById(R.id.songs_title);
        txtNameArtist = findViewById(R.id.songs_artist_name);
        songArt = findViewById(R.id.songs_cover_one);
        seekBarSong = findViewById(R.id.seekBar3);
        txtStartTime = findViewById(R.id.startTime);
        txtEndTime = findViewById(R.id.endTime);
    }

    // open fragment
    private void openFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ///Them music control tai day
        mFragmentTransaction.add(R.id.frame_music_contains, new MyMusicFragment());
        mFragmentTransaction.add(R.id.frame_music_mode, new ModeMusicFragment());
        mFragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentPaused = false;
        if (seekBarSong != null)
            seekBarSong.postDelayed(mUpdateProgress, 10);
    }

    @Override
    public void onBackPressed() {

        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.frame_music_contains);

        if (fragment != null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.remove(fragment);
            mFragmentTransaction.add(R.id.frame_music_contains, new MyMusicFragment());
            mFragmentTransaction.commit();
        } else {
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
        return super.onCreateOptionsMenu(menu);
    }


    private void setEventForUI() {
        //Bắt sự kiện cho button play
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                if (play_main.getVisibility() == View.VISIBLE) {
                    play_main.setVisibility(View.GONE);
                    pause_main.setVisibility(View.VISIBLE);
                }
                circleImageSong.startAnimation(mTotaleDisk);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                    }
                }, 200);
            }
        });

        //Bắt sự kiện cho button pause
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                if (pause_main.getVisibility() == View.VISIBLE) {
                    pause_main.setVisibility(View.GONE);
                    play_main.setVisibility(View.VISIBLE);
                }
                circleImageSong.clearAnimation();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                    }
                }, 200);
            }
        });

        //Bắt sự kiện cho button play main
        play_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_main.setVisibility(View.GONE);
                pause_main.setVisibility(View.VISIBLE);
                if (play.getVisibility() == View.VISIBLE) {
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
                circleImageSong.startAnimation(mTotaleDisk);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                    }
                }, 200);
            }
        });

        //Bắt sự kiện cho button pause main
        pause_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause_main.setVisibility(View.GONE);
                play_main.setVisibility(View.VISIBLE);
                if (pause.getVisibility() == View.VISIBLE) {
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }
                circleImageSong.clearAnimation();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.playOrPause();
                    }
                }, 200);
            }
        });

        //Bắt sự kiện cho button previous
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.previous();
                    }
                }, 200);
            }
        });

        //Bắt sự kiện cho button forward
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.next();
                    }
                }, 200);

            }
        });

        //Bắt sự kiện cho button trộn nhạc ngẫu nhiên
        btnShuffleOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShuffleOff.setVisibility(View.GONE);
                btnShuffleOn.setVisibility(View.VISIBLE);
                MusicPlayer.cycleShuffle();
            }
        });

        btnShuffleOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShuffleOff.setVisibility(View.VISIBLE);
                btnShuffleOn.setVisibility(View.GONE);
                MusicPlayer.cycleShuffle();
            }
        });

        //Bắt sự kiện cho button lặp lại bài hát
        btnRepeatOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRepeatOff.setVisibility(View.GONE);
                btnRepeatOne.setVisibility(View.VISIBLE);
                MusicPlayer.cycleRepeat();
            }
        });

        btnRepeatOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 btnRepeatOne.setVisibility(View.GONE);
                 btnRepeatAll.setVisibility(View.VISIBLE);
                MusicPlayer.cycleRepeat();
            }
        });

        btnRepeatAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRepeatAll.setVisibility(View.GONE);
                btnRepeatOff.setVisibility(View.VISIBLE);
                MusicPlayer.cycleRepeat();
            }
        });

        //Bắt sự kiện thay đổi seekBar
        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                txtStartTime.setText(formatTime.format(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayer.seek(seekBarSong.getProgress());
            }
        });
    }

    //Cập nhật thay đổi khi phát nhạt tại đây
    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        updateControl();
    }

    private void updateControl() {
        if (MusicPlayer.isPlaying()) {
            Song song = MusicPlayer.getSongCurrent();


            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(MusicUtils.getAlbumArtUri(song.albumId).toString(),
                    songArt, new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnLoading(R.drawable.ic_song_perform)
                            .resetViewBeforeLoading(true).build());

            imageLoader.displayImage(MusicUtils.getAlbumArtUri(song.albumId).toString(),
                    circleImageSong, new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnLoading(R.drawable.ic_song_perform)
                            .resetViewBeforeLoading(true).build());


            circleImageSong.startAnimation(mTotaleDisk);

            txtNameArtist.setText("   " + song.artistName);
            txtSongTitle.setText("   " + song.title);

            SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
            txtEndTime.setText(formatTime.format(song.duration));

            seekBarSong.setMax(song.duration);

            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);

            play_main.setVisibility(View.GONE);
            pause_main.setVisibility(View.VISIBLE);
        }else{
            circleImageSong.clearAnimation();
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);

            play_main.setVisibility(View.VISIBLE);
            pause_main.setVisibility(View.GONE);
        }
        seekBarSong.postDelayed(mUpdateProgress, 10);
    }


}
