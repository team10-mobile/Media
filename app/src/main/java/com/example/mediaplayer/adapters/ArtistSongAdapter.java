package com.example.mediaplayer.adapters;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.example.mediaplayer.dataloader.ArtistAlbumLoader;
import com.example.mediaplayer.dialogs.AddPlaylistDialog;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.utils.NavigationUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter để đỗ dữ liệu lên ArtistMusicFragment
 */
public class ArtistSongAdapter extends BaseSongAdapter<ArtistSongAdapter.ItemHolder> {

    private List<Song> arraylist;

    private AppCompatActivity mContext;

    private long artistID;

    private long[] songIDs;

    public ArtistSongAdapter(AppCompatActivity context, List<Song> arraylist, long artistID) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.artistID = artistID;
        this.songIDs = getSongIds();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //Nếu i==0 thì vị trí đầu tiên trong recycler view này sẽ làm header(chứa tất cả album của nghệ
        // sĩ), phần còn lại chứa tất cả bài hát
        if (i == 0) {
            View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_detail_albums_header, null);
            ItemHolder ml = new ItemHolder(v0);
            return ml;
        } else {
            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_song, null);
            ItemHolder ml = new ItemHolder(v2);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {

        if (getItemViewType(i) == 0) {
            //Tạo một recycler view chứa tất cả album của nghệ sĩ
            setUpAlbums(itemHolder.albumsRecyclerView);
        } else {

            Song localItem = arraylist.get(i);
            itemHolder.title.setText(localItem.title);
            itemHolder.album.setText(localItem.albumName);

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            imageLoader.displayImage(MusicUtils.getAlbumArtUri(localItem.albumId).toString(),
                    itemHolder.albumArt, new DisplayImageOptions.Builder()
                            .cacheInMemory(true).showImageOnLoading(R.drawable.ic_song_perform)
                            .resetViewBeforeLoading(true).build());
            setOnPopupMenuListener(itemHolder, i - 1);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, album;

        protected ImageView albumArt, menu;

        //Dùng để hiển thị album của nghệ sĩ nếu item này được dùng làm header
        protected RecyclerView albumsRecyclerView;

        public ItemHolder(View view) {
            super(view);
            this.albumsRecyclerView = view.findViewById(R.id.recycler_view_album);
            this.title =  view.findViewById(R.id.song_title);
            this.album = view.findViewById(R.id.song_album);
            this.albumArt = view.findViewById(R.id.albumArt);
            this.menu =  view.findViewById(R.id.popup_menu);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAll(mContext, getAdapterPosition(),
                            MusicUtils.IdType.Artist, arraylist.get(getAdapterPosition()), false);
                }
            }, 100);
        }
    }


    public long[] getSongIds() {
        List<Song> actualArraylist = new ArrayList<Song>(arraylist);
        //actualArraylist.remove(0);
        long[] ret = new long[actualArraylist.size()];
        for (int i = 0; i < actualArraylist.size(); i++) {
            ret[i] = actualArraylist.get(i).id;
        }
        return ret;
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
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
                                        position + 1,
                                        MusicUtils.IdType.Artist,
                                        arraylist.get(position+1),
                                        false);
                                break;

                            case R.id.popup_song_goto_album:
                                NavigationUtils.navigateToAlbum(mContext,
                                        arraylist.get(position+1).albumId,
                                        new Pair<View, String>(itemHolder.menu,
                                                "transition_album_art" + (position+1)));
                                break;

                            case R.id.popup_song_goto_artist:
                                NavigationUtils.navigateToArtist(mContext,
                                        arraylist.get(position + 1).artistId,
                                        new Pair<View, String>(itemHolder.menu,
                                                "transition_artist_art" + (position+1)));
                                break;

                            case R.id.popup_song_delete:
                                long[] deleteIds = {arraylist.get(position+1).id};
                                MusicUtils.showDeleteDialog(mContext,arraylist.get(position).title,
                                        deleteIds,
                                        ArtistSongAdapter.this,
                                        position+1);
                                break;

                            case R.id.popup_song_addto_playlist:
                                AddPlaylistDialog.newInstance(arraylist.get(position+1))
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

    private void setUpAlbums(RecyclerView albumsRecyclerview) {

        //Hiển thị album này theo chiều ngang
        albumsRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        albumsRecyclerview.setHasFixedSize(true);

        //to add spacing between cards
        albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(3));
        albumsRecyclerview.setNestedScrollingEnabled(false);


        //NOTE BUG
        //Adapter để hiển thị danh sách album của nghệ sĩ lên recycler view
        //Tham số nhận vào là một album được tìm theo id của nghệ sĩ và một context
        ArtistAlbumAdapter mAlbumAdapter = new ArtistAlbumAdapter(mContext, ArtistAlbumLoader.getAlbumsForArtist(mContext, artistID));
        albumsRecyclerview.setAdapter(mAlbumAdapter);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            //the padding from left
            outRect.left = space;


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return  0;
        return 1;
    }

    @Override
    public void removeSongAt(int i){
        arraylist.remove(i);
        updateDataSet(arraylist);
    }
}
