package com.example.mediaplayer.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.models.Playlist;

import java.util.List;
import java.util.Random;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> {
    private List<Playlist> arraylist;

    private Activity mContext;

    private boolean isGrid;

    private boolean showAuto;

    private int songCountInt;

    private long totalRuntime;

    private long firstAlbumID = -1;

    private int foregroundColor;

    int[] foregroundColors =
            {R.color.pink_transparent,
                    R.color.green_transparent,
                    R.color.blue_transparent,
                    R.color.red_transparent,
                    R.color.purple_transparent};

    public PlaylistAdapter(Activity context, List<Playlist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = true;
        this.showAuto = true;
        Random random = new Random();
        int rndInt = random.nextInt(foregroundColors.length);
        foregroundColor = foregroundColors[rndInt];

    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (isGrid) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_grid, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_list, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        Playlist localItem = arraylist.get(i);
        itemHolder.title.setText(localItem.name);
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, artist;

        protected ImageView albumArt;

        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.album_title);
            this.artist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
    public void updateDataSet(List<Playlist> arraylist) {
        this.arraylist.clear();
        this.arraylist.addAll(arraylist);
        notifyDataSetChanged();
    }
}
