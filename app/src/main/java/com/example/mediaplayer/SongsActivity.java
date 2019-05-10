package com.example.mediaplayer;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.mediaplayer.ClassLayer.Songs;
import com.example.mediaplayer.ClassLayer.SongsAdapter;

import java.util.ArrayList;

public class SongsActivity extends AppCompatActivity {

    private ArrayList<Songs> mArraySongs;
    private Intent mIntent;
    private SongsAdapter mSongAdapter;
    private ListView mListSong;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private ImageView mSort, mSearch, mScan;
    private EditText mTextSearch;

    // mapping views
    private void mapping()
    {
        mSort = findViewById(R.id.sort);
        mSearch = findViewById(R.id.search);
        mScan = findViewById(R.id.scan);
        mTextSearch = findViewById(R.id.text_search);
        mListSong = findViewById(R.id.list_songs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        mapping();
        openFragment();
        addSongs();
        clickListSong();
        sortSong();
        searchSong();
        scanSong();
    }
    // scan songs
    private void scanSong()
    {
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: scan songs
            }
        });
    }
    // search songs
    private void searchSong()
    {
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: search song
            }
        });
    }
    // sort songs
    private void sortSong()
    {
        mSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: sort songs
            }
        });
    }
    // todo: get position here and use putExtra into PlaySongActivity
    // click list of songs then get position of song to play song, add playlist or remove
    private void clickListSong()
    {
        mSongAdapter = new SongsAdapter(this,R.layout.songs,mArraySongs);
        mListSong.setAdapter(mSongAdapter);
        this.mListSong.setItemsCanFocus(false);
        mListSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final ImageButton imageButton = view.findViewById(R.id.button_menu);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowMenu(imageButton,position);
                    }
                });
                mIntent = new Intent(SongsActivity.this,PlaySongsActivity.class);
                mIntent.putExtra("position_song",position);
                startActivity(mIntent);
            }
        });
    }
    // todo: remove song, add playlist
    /**
     * @param position : position of song need to handle (remove / add playlist)
     * */
    private void ShowMenu(ImageButton imageButton, final int position)
    {
        PopupMenu popupMenu = new PopupMenu(this,imageButton);
        popupMenu.getMenuInflater().inflate(R.menu.remove_or_add_playlist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_add_playlist:
                        // todo: add playlist
                        break;
                    case R.id.menu_remove:
                        // todo: remove song
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    // add songs into array of song
    private void addSongs()
    {
        mArraySongs = new ArrayList<>();
        mArraySongs.add(new Songs("Ngồi hát đở buồn","Trúc Nhân","NhacCuaTui",R.raw.ngoihatdobuon));
        mArraySongs.add(new Songs("Về bên anh","Jack","NhacCuaTui",R.raw.vebenanh));
        mArraySongs.add(new Songs("Lớn rồi còn khóc nhè","Trúc Nhân","NhacCuaTui",R.raw.lonroiconkhocnhe));
        mArraySongs.add(new Songs("Thương em là điều anh không thể","Noo Phước Thịnh","NhacCuaTui"
                ,R.raw.thuongemladieuanhkhongthe));
    }
    // open fragment control music
    private void openFragment()
    {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.frame_music_contains_ver2,new ControlMusicFragment());
        mFragmentTransaction.commit();
    }

}
