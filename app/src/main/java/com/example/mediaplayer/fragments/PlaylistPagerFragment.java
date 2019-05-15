package com.example.mediaplayer.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.dataloader.LastAddedLoader;
import com.example.mediaplayer.dataloader.PlaylistLoader;
import com.example.mediaplayer.dataloader.PlaylistSongLoader;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.dataloader.TopTracksLoader;
import com.example.mediaplayer.models.Playlist;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.utils.Constants;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.utils.NavigationUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaylistPagerFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "pageNumber";

    private int[] foregroundColors =
            {R.color.pink_transparent,
            R.color.green_transparent,
            R.color.blue_transparent,
                    R.color.red_transparent,
                    R.color.purple_transparent};

    private int pageNumber, songCountInt, totalRuntime;
    private int foregroundColor;
    private long firstAlbumID = -1;
    private Playlist playlist;
    private TextView playlistame, songcount, playlistnumber, playlisttype, runtime;
    private ImageView playlistImage;
    private View foreground;
    private Context mContext;
    private boolean showAuto;

    public static PlaylistPagerFragment newInstance(int pageNumber) {
        PlaylistPagerFragment fragment = new PlaylistPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_pager, container, false);
        showAuto = true;
        List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);

        pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        playlist = playlists.get(pageNumber);

        playlistame =  view.findViewById(R.id.name);
        playlistnumber =  view.findViewById(R.id.number);
        songcount =  view.findViewById(R.id.songcount);
        runtime =  view.findViewById(R.id.runtime);
        playlisttype = view.findViewById(R.id.playlisttype);
        playlistImage =  view.findViewById(R.id.playlist_image);
        foreground = view.findViewById(R.id.foreground);

        playlistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Pair> tranitionViews = new ArrayList<>();
                tranitionViews.add(0, Pair.create((View) playlistame, "transition_playlist_name"));
                tranitionViews.add(1, Pair.create((View) playlistImage, "transition_album_art"));
                tranitionViews.add(2, Pair.create(foreground, "transition_foreground"));
                NavigationUtils.navigateToPlaylistDetail(getActivity(), getPlaylistType(), firstAlbumID, String.valueOf(playlistame.getText()), foregroundColor, playlist.id, tranitionViews);
            }
        });


        mContext = this.getContext();
        setUpPlaylistDetails();
        return view;
    }

    private void setUpPlaylistDetails() {
        playlistame.setText(playlist.name);

        int number = getArguments().getInt(ARG_PAGE_NUMBER) + 1;
        String playlistnumberstring;

        if (number > 9) {
            playlistnumberstring = String.valueOf(number);
        } else {
            playlistnumberstring = "0" + String.valueOf(number);
        }
        playlistnumber.setText(playlistnumberstring);

        Random random = new Random();
        int rndInt = random.nextInt(foregroundColors.length);

        foregroundColor = foregroundColors[rndInt];
        foreground.setBackgroundColor(foregroundColor);

        if (showAuto) {
            if (pageNumber <= 1)
                playlisttype.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new LoadPlaylistImage().execute("");
    }

    private String getPlaylistType() {
        if (showAuto) {
            switch (pageNumber) {
                case 0:
                    return Constants.NAVIGATE_PLAYLIST_LASTADDED;
                case 1:
                    return Constants.NAVIGATE_PLAYLIST_RECENT;
                default:
                    return Constants.NAVIGATE_PLAYLIST_USERCREATED;
            }
        } else return Constants.NAVIGATE_PLAYLIST_USERCREATED;
    }

    private class LoadPlaylistImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null) {
                if (showAuto) {
                    switch (pageNumber) {
                        case 0:
                            List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(getActivity());
                            songCountInt = lastAddedSongs.size();
                            for (Song song : lastAddedSongs) {
                                totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                            }
                            if (songCountInt != 0) {
                                firstAlbumID = lastAddedSongs.get(0).albumId;
                                return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                            } else return "nosongs";
                        case 1:
                            TopTracksLoader recentloader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.RecentSongs);
                            List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                            songCountInt = recentsongs.size();
                            for(Song song : recentsongs){
                                totalRuntime += song.duration / 1000;
                            }

                            if (songCountInt != 0) {
                                firstAlbumID = recentsongs.get(0).albumId;
                                return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                            } else return "nosongs";
                        default:
                            List<Song> playlistsongs =
                                    PlaylistSongLoader.getSongsInPlaylist(getActivity(), playlist.id);
                            songCountInt = playlistsongs.size();
                            for (Song song : playlistsongs) {
                                totalRuntime += song.duration;
                            }
                            if (songCountInt != 0) {
                                firstAlbumID = playlistsongs.get(0).albumId;
                                return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                            } else return "nosongs";

                    }
                } else {
                   /* List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getActivity(), playlist.id);
                    songCountInt = playlistsongs.size();
                    for (Song song : playlistsongs) {
                        totalRuntime += song.duration;
                    }
                    if (songCountInt != 0) {
                        firstAlbumID = playlistsongs.get(0).albumId;
                        return MusicUtils.getAlbumArtUri(firstAlbumID).toString();
                    } else return "nosongs";*/
                }
            } else return "context is null";
            return "";
        }

        @Override
        protected void onPostExecute(String uri) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            ImageLoader.getInstance().displayImage(uri, playlistImage,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_song_perform)
                            .resetViewBeforeLoading(true)
                            .build(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }
                    });
            songcount.setText(" " + String.valueOf(songCountInt) + " " + mContext.getString(R.string.songs));
            runtime.setText(" " + MusicUtils.makeShortTimeString(mContext, totalRuntime));
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
