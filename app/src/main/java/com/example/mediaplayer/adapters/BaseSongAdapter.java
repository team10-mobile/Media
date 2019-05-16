package com.example.mediaplayer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.utils.MusicUtils;

import java.util.List;

public class BaseSongAdapter<T extends  RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull T t, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void playAll(Context context, int position,
                        MusicUtils.IdType sourceType, Song song,
                        boolean shuffleMode){
        MusicPlayer.playAll(context,position,sourceType,song,shuffleMode);
    }

    public void removeSongAt(int i){}
    public void updateDataSet(List<Song> arraylist) {}

}
