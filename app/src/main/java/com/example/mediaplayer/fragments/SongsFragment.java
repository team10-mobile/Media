package com.example.mediaplayer.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activities.BaseActivity;
import com.example.mediaplayer.adapters.SongListAdapter;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.service.MusicStateListener;

public class SongsFragment extends Fragment implements MusicStateListener {

    private SongListAdapter mAdapter;

    private RecyclerView recyclerView;

    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ((BaseActivity)getActivity()).setMusicStateListener(this);

        //Chạy bất đồng bộ để list tất cả bài hát từ storage của máy
        new LoadSongs().execute("");
        return view;
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        //Khi service gửi broadcast thì thì base activity nhận được thông, và nó thông  báo
        //cập nhật thay đổi trạng thái bài hát đến tất cả fragment

        if(mAdapter != null) mAdapter.notifyDataSetChanged();
    }


    /**
     * AsyncTask để list tất cả bài hát từ storage của máy
     */
    private class LoadSongs extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            if(getActivity()!=null){
                mAdapter = new SongListAdapter((AppCompatActivity)getActivity(),
                        SongLoader.getAllSongs(getActivity()),false,false);
            }
            return "Excuted";
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView.setAdapter(mAdapter);
            if (getActivity() != null) {
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                        DividerItemDecoration.VERTICAL));
            }
        }
    }


}
