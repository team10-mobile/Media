package com.example.mediaplayer.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mediaplayer.ClassLayer.Songs;
import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.SongListAdapter;
import com.example.mediaplayer.adapters.SongsListAdapter;
import com.example.mediaplayer.dataloader.SongLoader;
import com.example.mediaplayer.models.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnlineMusicFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private SongListAdapter adapter;
    private RecyclerView mRecyclerView;
    private Context context;
    private ArrayList<Song> mSongs;
    private Button btnVie;
    private Button btnEng;
    private String collectionPath;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_online_music, null);
        mapping();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mSongs = new ArrayList<>();
        collectionPath ="Vie";
        //getDatabase();
        new getDatabase().execute("");
        btnEng.setOnClickListener(this);
        btnVie.setOnClickListener(this);
        return mView;
    }

    private void mapping()
    {
        context = getActivity();
        mRecyclerView = mView.findViewById(R.id.recycListSongOnline);
        btnVie = mView.findViewById(R.id.btnVieSong);
        btnEng = mView.findViewById(R.id.btnEngSong);
    }


    private class getDatabase extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            if (getActivity() != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(collectionPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot documents : task.getResult()) {
                                FirebaseFirestore tempDB = FirebaseFirestore.getInstance();
                                DocumentReference codesRef = tempDB.collection(collectionPath).document(documents.getId());
                                codesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<String> listField = new ArrayList<>();
                                            Map<String, Object> map = task.getResult().getData();
                                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                listField.add((String) entry.getValue());
                                            }
                                            long id = Integer.parseInt(documents.getId());
                                            Uri uri = Uri.parse(listField.get(2));
                                            Song song = new Song(id, 1, 1, listField.get(0),
                                                    listField.get(1), "", 1, 1, uri);
                                            mSongs.add(song);
                                            Toast.makeText(context, mSongs.size() + "", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                codesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        adapter = new SongListAdapter((AppCompatActivity) getActivity(), mSongs, false, false);
                                        mRecyclerView.setAdapter(adapter);
                                    }
                                });
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
            return "Excuted";
        }

    }
    public void onClick(View v) {
        // TODO : Zoom in zoom out animation for 2 buttons
        switch (v.getId())
        {
            case R.id.btnVieSong :
                if(!collectionPath.equals("Vie")) {
                    collectionPath = "Vie";
                    mSongs.clear();
                    new getDatabase().execute("");
                }
                break;
            case R.id.btnEngSong:
                if(!collectionPath.equals("Eng")){
                    collectionPath = "Eng";
                    mSongs.clear();
                    new getDatabase().execute("");
                }
                break;
        }
    }
}
