package com.example.mediaplayer.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.AlbumSongsAdapter;
import com.example.mediaplayer.dataloader.AlbumLoader;
import com.example.mediaplayer.dataloader.AlbumSongLoader;
import com.example.mediaplayer.models.Album;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.utils.MusicUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;


public class AlbumDetailFragment extends Fragment {

    private long albumID = -1;
    private ImageView albumArt, artistArt;
    private TextView albumTitle, albumDetails;
    private AppCompatActivity mContext;
    private RecyclerView recyclerView;
    private AlbumSongsAdapter mAdapter;
    private Toolbar toolbar;
    private Album album;

    //Làm nhiệm vụ bao bọc lấy thanh Toolbar, implement Collapsing App Bar.
    private CollapsingToolbarLayout collapsingToolbarLayout;

    //Về cơ bản thì nó là một LinearLayout theo chiều dọc, nó nhận nhiệm vụ bắt các sự kiện scroll
    //của các thẻ con bên trong nó Nhờ có AppBarLayout mà CollapsingToolbarLayout có thể scroll mượt mà.

    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private boolean loadFailed = false;
    private Context context;
    private int primaryColor = -1;

    public static AlbumDetailFragment newInstance(long id, boolean useTransition, String transitionName) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putLong("album_id", id);
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
            albumID = getArguments().getLong("album_id");
        }
        context = getActivity();
        mContext = (AppCompatActivity) context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.fragment_album_detail, container, false);

        albumArt =  view.findViewById(R.id.album_art);//Hinh cua album
        artistArt = view.findViewById(R.id.artist_art);//Hinh cua nghe si
        albumTitle = view.findViewById(R.id.album_title);//Tieu de cua album
        albumDetails = view.findViewById(R.id.album_details);
        toolbar = view.findViewById(R.id.toolbar);
        fab =  view.findViewById(R.id.fab);

        if (getArguments().getBoolean("transition")) {
            albumArt.setTransitionName(getArguments().getString("transition_name"));
        }

        recyclerView =  view.findViewById(R.id.recyclerview);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        appBarLayout = view.findViewById(R.id.app_bar);
        recyclerView.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        album = AlbumLoader.getAlbum(getActivity(), albumID);

        //Framwork imageloader
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        imageLoader.displayImage(MusicUtils.getAlbumArtUri(albumID).toString(),
                albumArt, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music)
                        .resetViewBeforeLoading(true).build());

        setUpEverything();

        return  view;
    }

    private void setUpEverything() {
        setupToolbar();
       // setAlbumDetails();
        setUpAlbumSongs();
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);//Hien thi widget de co the nhan quay lai
        collapsingToolbarLayout.setTitle(album.title);//Dat tieu de cho thanh toolbar
    }

    private void setAlbumDetails() {
        String songCount = MusicUtils.makeLabel(getActivity(), R.plurals.Nsongs, album.songCount);
        String year = (album.year != 0) ? (" - " + String.valueOf(album.year)) : "";
        albumTitle.setText(album.title);
        albumDetails.setText(album.artistName + " - " + songCount + year);
    }

    //List tat ca bai hat len recyclerView
    private void setUpAlbumSongs() {
        List<Song> songList = AlbumSongLoader.getSongsForAlbum(getActivity(), albumID);
        mAdapter = new AlbumSongsAdapter((AppCompatActivity)getActivity(), songList, albumID);
        //Set khoang cach giuaa cac song
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Su kien quay lai khi an nut dieu huong tren toolbar
        if(item.getItemId() == android.R.id.home){
            mContext.getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }


}
