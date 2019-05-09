package com.example.mediaplayer.ClassLayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mediaplayer.R;

import java.util.List;

public class SongsAdapter extends BaseAdapter {

    private Context mContext;
    private int mLayout;
    private List<Songs> mSongList;
    private LayoutInflater mLayoutInflater;


    public SongsAdapter(Context mContext, int mLayout, List<Songs> mSongList) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.mSongList = mSongList;
    }

    @Override
    public int getCount() {
        return mSongList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mLayoutInflater.inflate(mLayout,null);
        TextView mTitleSong = convertView.findViewById(R.id.name_song);
        TextView mNameArtist = convertView.findViewById(R.id.name_artist);
        Songs songs = mSongList.get(position);
        mTitleSong.setText(songs.getmTitle());
        mNameArtist.setText(songs.getmArtist());
        return convertView;
    }
}
