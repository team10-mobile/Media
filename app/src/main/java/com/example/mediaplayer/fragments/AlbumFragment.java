package com.example.mediaplayer.fragments;

import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.AlbumAdapter;
import com.example.mediaplayer.dataloader.AlbumLoader;

public class AlbumFragment extends Fragment {

    private AlbumAdapter mAdapter;

    private RecyclerView recyclerView;

    private GridLayoutManager layoutManager;

    private RecyclerView.ItemDecoration itemDecoration;

    private boolean isGrid = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setLayoutManager();
        if (getActivity() != null)
            new LoadAlbums().execute("");
        return view;
    }

    private void setLayoutManager() {
        if (isGrid) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    private class LoadAlbums extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                mAdapter = new AlbumAdapter(getActivity(), AlbumLoader.getAllAlbums(getActivity()));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            //to add spacing between cards
            if (getActivity() != null) {
                setItemDecoration();
            }
        }
    }

    private void setItemDecoration() {
        if (isGrid) {

            itemDecoration = new SpacesItemDecoration(3);
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
}
