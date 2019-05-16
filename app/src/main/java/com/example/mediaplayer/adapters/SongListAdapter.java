package com.example.mediaplayer.adapters;


import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dialogs.AddPlaylistDialog;
import com.example.mediaplayer.fragments.DownloadFragment;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.utils.NavigationUtils;
import com.example.mediaplayer.widgets.MusicVisualizer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter để đổ nhạc lên recycler view
 */
public class SongListAdapter extends BaseSongAdapter<SongListAdapter.ItemHolder> implements Filterable {

    public boolean isOnline;

    public int currentlyPlayingPosition;//Vị trí của bái hát đang phát hiện tại

    private List<Song> arraylist;//Danh sách bài hát hiện có

    private AppCompatActivity mContext;

    private long[] songIDs;//Mảng chứa tất cả id của bài hát

    private boolean isPlaylist;//Cho biết có playlist nào đang được phát hay không

    private MusicUtils.IdType sourceType = MusicUtils.IdType.NA;

    private boolean animate;

    private int lastPosition = -1;

    private long playlistId;//Id của playlist

    private List<Song> mStringFilterList;     // Dùng trong việc tìm kiếm bài hát
    private ValueFilter valueFilter;

    public SongListAdapter(AppCompatActivity context, List<Song> arraylist,
                           boolean isPlaylistSong, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isPlaylist = isPlaylistSong;

        if(isPlaylistSong) sourceType= MusicUtils.IdType.Playlist;

        this.songIDs = getSongIds();
        this.animate = animate;
        this.mStringFilterList = arraylist;
    }

    //Chuyen doi view holder thanh javacode
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (isPlaylist) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_song_playlist, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_song, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    //Method này sẽ được gọi khi khởi tạo adapter đổ dữ liệu lên recycler view và
    // kể cả khi người dùng tương tác với view
    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        Song song = arraylist.get(i);
            itemHolder.title.setText(song.title);
            itemHolder.artist.setText(song.artistName);


        //Hiệu ứng chuyển động trên  item khi item này đang được chọn(phát)
       if(MusicPlayer.getCurrentSongId() == song.id) {
           if(MusicPlayer.isPlaying()) {
               itemHolder.visualizer.setColor(Color.RED);
               itemHolder.visualizer.setVisibility(View.VISIBLE);
           }
           else {
               itemHolder.visualizer.setVisibility(View.GONE);
           }
        }else {
           itemHolder.visualizer.setVisibility(View.GONE);
       }

        //Framwork Imageloader hiển thị ảnh cua bài hát
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        imageLoader.displayImage(MusicUtils.getAlbumArtUri(song.albumId).toString(),
                itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_song_perform)
                        .resetViewBeforeLoading(true).build());

        setOnPopupMenuListener(itemHolder,i);
    }

    //Hiển thị popup menu khi nhấn vào biểu tượng ic_more_vertical trên item và bắt sự kiện cho nó
    private void setOnPopupMenuListener(final ItemHolder itemHolder, final int postion){
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final   PopupMenu popupMenu = new PopupMenu(mContext,v);
              popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                  @Override
                  public boolean onMenuItemClick(MenuItem item) {
                      switch (item.getItemId()){

                          case R.id.popup_song_play:
                              MusicPlayer.playAll(mContext,
                                      postion,
                                      sourceType,
                                      arraylist.get(postion),
                                      false);
                              break;

                          case R.id.popup_song_goto_album:
                              NavigationUtils.navigateToAlbum(mContext,
                                      arraylist.get(postion).albumId,
                                      new Pair<View, String>(itemHolder.albumArt,
                                              "transition_album_art" + postion));
                              break;

                          case R.id.popup_song_goto_artist:
                              NavigationUtils.navigateToArtist(mContext,
                                      arraylist.get(postion).artistId,
                                      new Pair<View, String>(itemHolder.albumArt,
                                              "transition_artist_art" + postion));
                              break;

                          case R.id.popup_song_delete:
                              long[] deleteIds = {arraylist.get(postion).id};
                              MusicUtils.showDeleteDialog(mContext,arraylist.get(postion).title,
                                      deleteIds,
                                      SongListAdapter.this,
                                      postion);
                              break;

                          case R.id.popup_song_addto_playlist:
                              AddPlaylistDialog.newInstance(arraylist.get(postion))
                                      .show(mContext.getSupportFragmentManager(),
                                      "ADD_PLAYLIST");
                              break;
                          case R.id.popup_download:
                              Uri uri = arraylist.get(postion).uri;
                              long lastDownload =-1;
                              DownloadManager mgr = null;
                              mgr = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                              Environment
                                      .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                      .mkdirs();

                              lastDownload = mgr.enqueue(new DownloadManager.Request(uri)
                                      .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                              DownloadManager.Request.NETWORK_MOBILE)
                                      .setAllowedOverRoaming(false)
                                      .setTitle(arraylist.get(postion).title)
                                      .setDescription("Downloading, Please Wait...s")
                                      .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                              arraylist.get(postion).title+".mp3"));
                      }
                      return false;
                  }
              });

              if(isOnline)
                  popupMenu.inflate(R.menu.popup_online_selection);
              else
                  popupMenu.inflate(R.menu.popup_song);

              popupMenu.show();
              //Nếu item là một playlist thì hiển thị lên popup menu item cho phép xóa đi playlist
              if(isPlaylist){
                  popupMenu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
              }
            }
        });
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Song> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).title.toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mStringFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            arraylist = (List<Song>) results.values;
            notifyDataSetChanged();
        }

    }

    public  class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, artist;

        protected ImageView albumArt, popupMenu;

        private MusicVisualizer visualizer;

        public ItemHolder(View view) {
            super(view);
            this.title     = view.findViewById(R.id.song_title);
            this.artist    = view.findViewById(R.id.song_artist);
            this.albumArt  = view.findViewById(R.id.albumArt);
            this.popupMenu = view.findViewById(R.id.popup_menu);
            visualizer     = view.findViewById(R.id.visualizer);
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

                    //Chạy thread này để cập nhật lại hiệu ứng cho item
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(getAdapterPosition());
                            notifyDataSetChanged();
                        }
                    }, 50);
                }
            }, 100);
        }
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

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public void removeSongAt(int i) {
        arraylist.remove(i);
        updateDataSet(arraylist);
    }
}
