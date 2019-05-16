package com.example.mediaplayer.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.SongListAdapter;
import com.example.mediaplayer.dataloader.LastAddedLoader;
import com.example.mediaplayer.dataloader.PlaylistLoader;
import com.example.mediaplayer.dataloader.PlaylistSongLoader;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.dataloader.TopTracksLoader;
import com.example.mediaplayer.fragments.PlaylistPagerFragment;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicStateListener;
import com.example.mediaplayer.utils.Constants;
import com.example.mediaplayer.utils.MusicUtils;
import com.example.mediaplayer.widgets.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

public class TestFragment extends Fragment implements MusicStateListener {

    private long playlistID;

    private HashMap<String, Runnable> playlistsMap = new HashMap<>();

    private AppCompatActivity mContext ;

    private SongListAdapter mAdapter;

    private RecyclerView recyclerView;

    private ImageView blurFrame;

    private TextView playlistname;

    private View foreground;

    private boolean animate;

    private Runnable playlistLastAdded = new Runnable() {
        public void run() {
            new TestFragment.loadLastAdded().execute("");
        }
    };

    private Runnable playlistRecents = new Runnable() {
        @Override
        public void run() {
            new TestFragment.loadRecentlyPlayed().execute("");

        }
    };

    private Runnable playlistUsercreated = new Runnable() {
        @Override
        public void run() {
            new TestFragment.loadUserCreatedPlaylist().execute("");

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_playlist_detail, container, false);
        mContext= (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        mContext.setSupportActionBar(toolbar);
        mContext.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext.getSupportActionBar().setTitle("");

        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_LASTADDED, playlistLastAdded);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_RECENT, playlistRecents);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_USERCREATED, playlistUsercreated);

        recyclerView = view.findViewById(R.id.recyclerview);
        blurFrame = view.findViewById(R.id.blurFrame);
        playlistname = view.findViewById(R.id.name);
        foreground = view.findViewById(R.id.foreground);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        Bundle bundle = this.getArguments();
        String action  = bundle.getString(PlaylistPagerFragment.PLAYLIST_TYPE);
        playlistname.setText(bundle.getString(PlaylistPagerFragment.PLAYLIST_NAME));
        if(action.equals(Constants.NAVIGATE_PLAYLIST_LASTADDED)) {
            loadLastAdded.execute(playlistLastAdded);
        }else if(action.equals(Constants.NAVIGATE_PLAYLIST_RECENT)){
            loadRecentlyPlayed.execute(playlistRecents);
        }else if(action.equals(Constants.NAVIGATE_PLAYLIST_USERCREATED)){
            loadUserCreatedPlaylist.execute(playlistUsercreated);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void loadBitmap(String uri) {
        ImageLoader.getInstance().displayImage(uri, blurFrame,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_song_perform)
                        .resetViewBeforeLoading(true)
                        .build());
    }

    private void setRecyclerViewAdapter() {
        recyclerView.setAdapter(mAdapter);
        if (animate && MusicUtils.isLollipop()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
                }
            }, 250);
        } else
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
    }

    private class loadLastAdded extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            List<Song> lastAdded = LastAddedLoader.getLastAddedSongs(mContext);
            mAdapter = new SongListAdapter(mContext, lastAdded, true, animate);
            mAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private void setRecyclerViewAapter() {
        recyclerView.setAdapter(mAdapter);
        if (animate && MusicUtils.isLollipop()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
                }
            }, 250);
        } else
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
    }

    private class loadRecentlyPlayed extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            TopTracksLoader loader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
            List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
            mAdapter = new SongListAdapter(mContext, recentsongs, true, animate);
            mAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();

        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadUserCreatedPlaylist extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, playlistID);
            mAdapter = new SongListAdapter(mContext, playlistsongs, true, animate);
            mAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class SimplelTransitionListener
            implements Transition.TransitionListener {
        public void onTransitionCancel(Transition paramTransition) {
        }

        public void onTransitionEnd(Transition paramTransition) {
        }

        public void onTransitionPause(Transition paramTransition) {
        }

        public void onTransitionResume(Transition paramTransition) {
        }

        public void onTransitionStart(Transition paramTransition) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_playlist_detail,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }




    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mContext.getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_delete_playlist:
                showDeletePlaylistDialog();
                break;
            case R.id.action_clear_auto_playlist:
                clearAutoPlaylists();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeletePlaylistDialog() {
        new MaterialDialog.Builder(mContext)
                .title("Delete playlist?")
                .content("Are you sure you want to delete playlist " + playlistname.getText().toString() + " ?")
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PlaylistLoader.deletePlaylists(mContext, playlistID);
                        Intent returnIntent = new Intent();
                        mContext.setResult(Activity.RESULT_OK, returnIntent);
                        mContext.finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private  String action;

    private void clearAutoPlaylists() {
        switch (action) {
            case Constants.NAVIGATE_PLAYLIST_LASTADDED:
                MusicUtils.clearLastAdded(mContext);
                break;
            case Constants.NAVIGATE_PLAYLIST_RECENT:
                MusicUtils.clearRecent(mContext);
                break;

        }
        Intent returnIntent = new Intent();
        mContext.setResult(Activity.RESULT_OK, returnIntent);
        mContext.finish();
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }


}
