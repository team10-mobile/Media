package com.example.mediaplayer.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplayer.ClassLayer.Songs;
import com.example.mediaplayer.R;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.utils.MusicUtils;

import java.util.ArrayList;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.SongsViewHolder> {
    private ArrayList<Songs> mSongs;
    private Context mContext;

    public SongsListAdapter(Context context, ArrayList<Songs> songs) {
        mContext = context;
        mSongs = songs;
    }

    public void add(Songs songs)
    {
        mSongs.add(songs);
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.songs, viewGroup, false);
        SongsViewHolder viewHolder = new SongsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder songsViewHolder, int i) {
        songsViewHolder.bindSongs(mSongs.get(i));
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mAvatar;
        private TextView mTitle;
        private TextView mArtist;
        private Uri mUri;
        //private Context mContext;

        public SongsViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.name_song);
            mArtist = itemView.findViewById(R.id.name_artist);
            mAvatar = itemView.findViewById(R.id.img_song);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindSongs(Songs songs) {
            mUri = songs.getUri();
            mTitle.setText(songs.getmTitle());
            mArtist.setText(songs.getmArtist());
            mAvatar.setImageResource(R.drawable.ic_song_perform);
        }

        @Override
        public void onClick(View v) {
            // TODO : Play music
            Toast.makeText(mContext,"onClick " + getAdapterPosition() + " " + mUri,Toast.LENGTH_SHORT).show();

        }

    }

}