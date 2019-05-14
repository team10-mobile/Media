package com.example.mediaplayer.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.ArtistSongAdapter;
import com.example.mediaplayer.dataloader.ArtistLoader;
import com.example.mediaplayer.dataloader.ArtistSongLoader;
import com.example.mediaplayer.models.Artist;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.utils.MusicUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Fragment chứa chi tiết thông tin về nghệ sĩ
 */
public class ArtistDetailFragment extends Fragment {

    private long artistID = -1;
    private ImageView artistArt;//Hình ảnh trong AppBarLayout
    private AppCompatActivity mContext;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ArtistSongAdapter mAdapter;
    private Context context;

    public static ArtistDetailFragment newInstance(long id, boolean useTransition, String transitionName) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        Bundle args = new Bundle();
        args.putLong("artist_id", id);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            artistID = getArguments().getLong("artist_id");
        }
        context = getActivity();
        mContext = (AppCompatActivity) context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_artist_detail, container, false);

        artistArt = view.findViewById(R.id.artist_art);
        collapsingToolbarLayout =  view.findViewById(R.id.collapsing_toolbar);
        appBarLayout = view.findViewById(R.id.app_bar);

        if (getArguments().getBoolean("transition")) {
            artistArt.setTransitionName(getArguments().getString("transition_name"));
        }

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        setupToolbar();
        setUpArtistDetails();

        //Tạo một fragment con nằm trong fragment này để chứa danh sách bài hát, album của nghệ sĩ
        getChildFragmentManager().beginTransaction().replace(R.id.container,
                ArtistMusicFragment.newInstance(artistID)).commit();

        return view;
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpArtistDetails() {
         final Artist artist = ArtistLoader.getArtist(getActivity(), artistID);

         //2 cái này để dùng trong sự kiện click popup, để di chuyển tới playlist
        List<Song> songList = ArtistSongLoader.getSongsForArtist(getActivity(), artistID);
        mAdapter = new ArtistSongAdapter(getActivity(), songList, artistID);

        collapsingToolbarLayout.setTitle(artist.name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Sự kiện quay pop stack fragment khi ấn nút điều hướng trên toolbar
        if(item.getItemId() == android.R.id.home){
            mContext.getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
