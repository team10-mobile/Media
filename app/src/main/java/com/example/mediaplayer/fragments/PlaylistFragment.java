package com.example.mediaplayer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.PlaylistAdapter;
import com.example.mediaplayer.dataloader.PlaylistLoader;
import com.example.mediaplayer.dialogs.CreatePlaylistDialog;
import com.example.mediaplayer.models.Playlist;
import com.example.mediaplayer.utils.Constants;
import com.example.mediaplayer.widgets.MultiViewPager;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private int playlistcount;

    private FragmentStatePagerAdapter adapter;

    private MultiViewPager pager;

    private RecyclerView recyclerView;

    private GridLayoutManager layoutManager;

    private RecyclerView.ItemDecoration itemDecoration;

    private boolean isGrid;

    private boolean isDefault;

    private boolean showAuto;

    private PlaylistAdapter mAdapter;

    private List<Playlist> playlists = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGrid = false;
        isDefault = true;
        showAuto = true;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        Toolbar toolbar =  view.findViewById(R.id.toolbar);
        pager =  view.findViewById(R.id.playListPager);
        recyclerView = view.findViewById(R.id.recyclerview);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.playlists);
        toolbar.setBackgroundColor(getResources().getColor(R.color.blue_transparent));
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            initPager();
        } else {
            initRecyclerView();
        }

        return view;
    }

    private void initPager() {
        pager.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setAdapter(null);
        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return playlistcount;
            }

            @Override
            public Fragment getItem(int position) {
                return PlaylistPagerFragment.newInstance(position);
            }

        };
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
    }

    private void initRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        pager.setVisibility(View.GONE);
        setLayoutManager();
        mAdapter = new PlaylistAdapter(getActivity(), playlists);

        recyclerView.setAdapter(mAdapter);
        //to add spacing between cards
        if (getActivity() != null) {
            setItemDecoration();
        }
    }

    private void setLayoutManager() {
        if (isGrid) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setItemDecoration() {
        if (isGrid) {
            itemDecoration = new SpacesItemDecoration(10);
        } else {
            itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        }
        recyclerView.addItemDecoration(itemDecoration);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {


            outRect.left = space;
            outRect.top = space;
            outRect.right = space;
            outRect.bottom = space;

        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_playlist:
                CreatePlaylistDialog.newInstance().show(getChildFragmentManager(), "CREATE_PLAYLIST");
                return true;
            case R.id.menu_show_as_list:
                isGrid = false;
                isDefault = false;
                initRecyclerView();
                updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                isGrid = true;
                isDefault = false;
                initRecyclerView();
                updateLayoutManager(2);
                return true;
            case R.id.menu_show_as_default:
                isDefault = true;
                initPager();
                return true;
                default:
                    reloadPlaylists();
                    getActivity().invalidateOptionsMenu();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void updatePlaylists(final long id) {
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            adapter.notifyDataSetChanged();
            if (id != -1) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < playlists.size(); i++) {
                            long playlistid = playlists.get(i).id;
                            if (playlistid == id) {
                                pager.setCurrentItem(i);
                                break;
                            }
                        }
                    }
                }, 200);
            }

        } else {
            mAdapter.updateDataSet(playlists);
        }
    }

    public void reloadPlaylists() {
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);
        playlistcount = playlists.size();

        if (isDefault) {
            initPager();
        } else {
            initRecyclerView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTION_DELETE_PLAYLIST) {
            if (resultCode == Activity.RESULT_OK) {
                reloadPlaylists();
            }

        }
    }
    private void updateLayoutManager(int column) {
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.setAdapter(new PlaylistAdapter(getActivity(), PlaylistLoader.getPlaylists(getActivity(), showAuto)));
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        setItemDecoration();
    }


}
