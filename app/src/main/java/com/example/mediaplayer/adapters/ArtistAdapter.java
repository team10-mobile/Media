package com.example.mediaplayer.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.models.Artist;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.utils.NavigationUtils;

import java.util.List;


/**
 * Adapter hiển thị thông tin nghệ sĩ, số lượng album và số bài hát của nghệ sĩ lên
 * recycler view
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder> {

    private List<Artist> arraylist;

    private Activity mContext;

    private boolean isGrid;

    public ArtistAdapter(Activity context, List<Artist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = true;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (isGrid) {
            //Hiển thị kiểu grid, có 2 hình mỗi hàng
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_grid, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            //Hiển thị kiểu list view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        final Artist artist = arraylist.get(i);
        itemHolder.name.setText(artist.name);//Thiết lặp hình ảnh cho một artist
        String albumNmber = MusicUtils.makeLabel(mContext, R.plurals.Nalbums, artist.albumCount);
        String songCount = MusicUtils.makeLabel(mContext, R.plurals.Nsongs, artist.songCount);
        itemHolder.albums.setText(MusicUtils.makeCombinedString(mContext, albumNmber, songCount));//Thiết lặp số lượng album và bài hát cho nghệ sĩ

        if (MusicUtils.isLollipop())
            itemHolder.artistImage.setTransitionName("transition_artist_art" + i);

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView name, albums;
        protected ImageView artistImage;
        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.artist_name);
            this.albums = (TextView) view.findViewById(R.id.album_song_count);
            this.artistImage = (ImageView) view.findViewById(R.id.artistImage);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Mở một fragment mới chứa chi tiết thông tin về nghệ sĩ
            NavigationUtils.navigateToArtist(mContext, arraylist.get(getAdapterPosition()).id,
                    new Pair<View, String>(artistImage, "transition_artist_art" + getAdapterPosition()));
        }

    }

}
