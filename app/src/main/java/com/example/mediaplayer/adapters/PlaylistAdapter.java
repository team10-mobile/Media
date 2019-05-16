package com.example.mediaplayer.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dataloader.LastAddedLoader;
import com.example.mediaplayer.dataloader.PlaylistSongLoader;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.dataloader.TopTracksLoader;
import com.example.mediaplayer.models.Playlist;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.utils.Constants;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.utils.NavigationUtils;
import com.example.mediaplayer.utils.PreferencesUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
        /*this.isGrid = true;
        this.showAuto = true;*/
        this.isGrid =
                PreferencesUtility.getInstance(mContext).getPlaylistView() == Constants.PLAYLIST_VIEW_GRID;
        this.showAuto =
                PreferencesUtility.getInstance(mContext).showAutoPlaylist();
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
    public void onBindViewHolder(@NonNull final ItemHolder itemHolder, int i) {
        Playlist localItem = arraylist.get(i);
        itemHolder.title.setText(localItem.name);


        String s = getAlbumArtUri(i, localItem.id);
        itemHolder.albumArt.setTag(firstAlbumID);
        ImageLoader.getInstance().displayImage(s, itemHolder.albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_song_perform)
                        .resetViewBeforeLoading(true)
                        .build(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (isGrid) {
                            new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    if (swatch != null) {
                                        int color = swatch.getRgb();
                                        itemHolder.footer.setBackgroundColor(color);
                                        int textColor = MusicUtils.getBlackWhiteColor(swatch.getTitleTextColor());
                                        itemHolder.title.setTextColor(textColor);
                                        itemHolder.artist.setTextColor(textColor);
                                    } else {
                                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                        if (mutedSwatch != null) {
                                            int color = mutedSwatch.getRgb();
                                            itemHolder.footer.setBackgroundColor(color);
                                            int textColor = MusicUtils.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                                            itemHolder.title.setTextColor(textColor);
                                            itemHolder.artist.setTextColor(textColor);
                                        }
                                    }


                                }
                            });
                        }

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (isGrid) {
                            itemHolder.footer.setBackgroundColor(0);
                            if (mContext != null) {
                                int textColorPrimary = Color.LTGRAY;
                                itemHolder.title.setTextColor(textColorPrimary);
                                itemHolder.artist.setTextColor(textColorPrimary);
                            }
                        }
                    }
                });


        itemHolder.artist.setText(" " + String.valueOf(songCountInt) + " "
                + mContext.getString(R.string.songs) + " - "
                + MusicUtils.makeShortTimeString(mContext,totalRuntime));

        if (MusicUtils.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);
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
            this.title    = view.findViewById(R.id.album_title);
            this.artist   =  view.findViewById(R.id.album_artist);
            this.albumArt = view.findViewById(R.id.album_art);
            this.footer   = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           // NavigationUtils.navigateToPlaylistDetail(mContext, getPlaylistType(getAdapterPosition()), (long) albumArt.getTag(), String.valueOf(title.getText()), foregroundColor, arraylist.get(getAdapterPosition()).id, null);
        }
    }

    public void updateDataSet(List<Playlist> arraylist) {
        this.arraylist.clear();
        this.arraylist.addAll(arraylist);
        notifyDataSetChanged();
    }

    private String getPlaylistType(int position) {
        if (showAuto) {
            switch (position) {
                case 0:
                    return Constants.NAVIGATE_PLAYLIST_LASTADDED;
                case 1:
                    return Constants.NAVIGATE_PLAYLIST_RECENT;
                default:
                    return Constants.NAVIGATE_PLAYLIST_USERCREATED;
            }
        } else return Constants.NAVIGATE_PLAYLIST_USERCREATED;
    }

    private String getAlbumArtUri(int position, long id) {
        if (mContext != null) {
            firstAlbumID = -1;
            if (showAuto) {
                switch (position) {
                    case 0:
                        List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(mContext);
                        songCountInt = lastAddedSongs.size();
                        totalRuntime = 0;
                        for(Song song : lastAddedSongs){
                            totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = lastAddedSongs.get(0).albumId;
                            return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 1:
                        TopTracksLoader recentloader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
                        List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = recentsongs.size();
                        totalRuntime = 0;
                        for(Song song : recentsongs){
                            totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = recentsongs.get(0).albumId;
                            return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 2:
                        TopTracksLoader topTracksLoader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.TopTracks);
                        List<Song> topsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = topsongs.size();
                        totalRuntime = 0;
                        for(Song song : topsongs){
                            totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = topsongs.get(0).albumId;
                            return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    default:
                        List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                        songCountInt = playlistsongs.size();
                        totalRuntime = 0;
                        for(Song song : playlistsongs){
                            totalRuntime += song.duration;
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = playlistsongs.get(0).albumId;
                            return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";

                }
            } else {
                List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                songCountInt = playlistsongs.size();
                totalRuntime = 0;
                for(Song song : playlistsongs){
                    totalRuntime += song.duration;
                }

                if (songCountInt != 0) {
                    firstAlbumID = playlistsongs.get(0).albumId;
                    return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                } else return "nosongs";
            }
        }
        return null;
    }

}
