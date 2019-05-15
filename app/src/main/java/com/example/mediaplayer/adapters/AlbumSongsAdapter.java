package com.example.mediaplayer.adapters;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dialogs.AddPlaylistDialog;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.utils.NavigationUtils;

import java.util.List;

//Lop xem chi tiet co bai hat co trong album
public class AlbumSongsAdapter extends BaseSongAdapter<AlbumSongsAdapter.ItemHolder> {

    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long albumID;
    private long[] songIDs;

    public AlbumSongsAdapter(AppCompatActivity context, List<Song> arraylist, long albumID) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.songIDs = getSongIds();
        this.albumID = albumID;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        Song localItem = arraylist.get(i);
        itemHolder.title.setText(localItem.title);
        itemHolder.duration.setText(MusicUtils.makeShortTimeString(mContext, (localItem.duration) / 1000));
        int tracknumber = localItem.trackNumber;
        if (tracknumber == 0) {
            itemHolder.trackNumber.setText("-");
        } else itemHolder.trackNumber.setText(String.valueOf(tracknumber));

        setOnPopupMenuListener(itemHolder, i);
    }

    private void setOnPopupMenuListener(final ItemHolder itemHolder, final int position) {
        itemHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext,
                                        position,
                                        MusicUtils.IdType.NA,
                                        arraylist.get(position),
                                        false);
                                break;

                            case R.id.popup_song_goto_album:
                                NavigationUtils.navigateToAlbum(mContext,
                                        arraylist.get(position).albumId,
                                        new Pair<View, String>(itemHolder.menu,
                                                "transition_album_art" + position));
                                break;

                            case R.id.popup_song_goto_artist:
                                NavigationUtils.navigateToArtist(mContext,
                                        arraylist.get(position).artistId,
                                        new Pair<View, String>(itemHolder.menu,
                                                "transition_artist_art" + position));
                                break;
                            case R.id.popup_song_delete:
                                long[] deleteIds = {arraylist.get(position).id};
                                MusicUtils.showDeleteDialog(mContext,arraylist.get(position).title,
                                        deleteIds,
                                        AlbumSongsAdapter.this,
                                        position);
                                break;

                            case R.id.popup_song_addto_playlist:
                                AddPlaylistDialog.newInstance(arraylist.get(position))
                                        .show(mContext.getSupportFragmentManager(),
                                                "ADD_PLAYLIST");
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);
                menu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }
        return ret;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, duration, trackNumber;
        protected ImageView menu;

        public ItemHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.song_title);
            this.duration = view.findViewById(R.id.song_duration);
            this.trackNumber =  view.findViewById(R.id.trackNumber);
            this.menu = view.findViewById(R.id.popup_menu);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAll(mContext, getAdapterPosition(),
                            MusicUtils.IdType.NA, arraylist.get(getAdapterPosition()), false);
                }
            }, 100);
        }
    }

    @Override
    public void removeSongAt(int i){
        arraylist.remove(i);
        updateDataSet(arraylist);
    }
}
