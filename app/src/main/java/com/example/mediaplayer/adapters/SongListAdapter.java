package com.example.mediaplayer.adapters;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activities.BaseActivity;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicPlayer;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.widgets.MusicVisualizer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.List;

/**
 * Adapter để đổ nhạc lên recycler view
 */
public class SongListAdapter extends BaseSongAdapter<SongListAdapter.ItemHolder> {

    public int currentlyPlayingPosition;//Vị trí của bái hát đang phát hiện tại

    private List<Song> arraylist;//Danh sách bài hát hiện có

    private AppCompatActivity mContext;

    private long[] songIDs;//Mảng chứa tất cả id của bài hát

    private boolean isPlaylist;//Cho biết có playlist nào đang được phát hay không

    private boolean animate;

    private int lastPosition = -1;

    private long playlistId;//Id của playlist


    public SongListAdapter(AppCompatActivity context, List<Song> arraylist, boolean isPlaylistSong, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isPlaylist = isPlaylistSong;
        this.songIDs = getSongIds();
        this.animate = animate;
    }

    //Chuyen doi view holder thanh javacode
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (isPlaylist) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_playlist, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
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
    private void setOnPopupMenuListener(ItemHolder itemHolder,final int postion){
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final   PopupMenu popupMenu = new PopupMenu(mContext,v);
              popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                  @Override
                  public boolean onMenuItemClick(MenuItem item) {
                      return false;
                  }
              });

              popupMenu.inflate(R.menu.popup_song);
              popupMenu.show();
              //Nếu item là một playlist thì hiển thị lên popup menu item cho phép xóa đi playlist
              if(isPlaylist){
                  popupMenu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
              }
            }
        });
    }

    public  class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, artist;

        protected ImageView albumArt, popupMenu;

        private MusicVisualizer visualizer;

        public ItemHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.song_title);
            this.artist = view.findViewById(R.id.song_artist);
            this.albumArt = view.findViewById(R.id.albumArt);
            this.popupMenu = view.findViewById(R.id.popup_menu);
            visualizer = view.findViewById(R.id.visualizer);
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
}
