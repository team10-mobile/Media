package com.example.mediaplayer.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.mediaplayer.R;
import com.example.mediaplayer.adapters.SongListAdapter;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.service.MusicPlayer;
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

public class OnlineMusicFragment extends Fragment implements View.OnClickListener, Animation.AnimationListener {

    private View mView;
    private SongListAdapter adapter;
    private RecyclerView mRecyclerView;
    private Context context;
    private ArrayList<Song> mSongs;
    private Button btnVie;
    private Button btnEng;
    private String collectionPath;
    private Animation animZoomIn;
    private Animation animZoomOut;
    private LinearLayout mLayoutScreenLoad;
    private LinearLayout mLayoutMain;
    private SearchView searchView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_online_music, null);
        mapping();
        mLayoutScreenLoad.getParent().bringChildToFront(mLayoutMain);
        if(isNetworkAvailable() && isOnline()) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mSongs = new ArrayList<>();
            collectionPath = "Vie";
            new getDatabase().execute("");
            btnEng.setOnClickListener(this);
            btnVie.setOnClickListener(this);
            searchView.setActivated(true);
            searchView.onActionViewExpanded();
            searchView.setIconified(false);
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
            btnEng.startAnimation(animZoomOut);
        }
        else
            Toast.makeText(context,"Internet Not Available!!!",Toast.LENGTH_SHORT).show();
        return mView;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Check Online State","Err ",e);
        }
        return false;
    }
    private void mapping()
    {
        context = getActivity();
        mLayoutScreenLoad = mView.findViewById(R.id.layoutScreenload);
        mLayoutMain = mView.findViewById(R.id.layoutOnline);
        mRecyclerView = mView.findViewById(R.id.recycListSongOnline);
        btnVie = mView.findViewById(R.id.btnVieSong);
        btnEng = mView.findViewById(R.id.btnEngSong);
        searchView = mView.findViewById(R.id.edtFindSong);
        animZoomIn = AnimationUtils.loadAnimation(context,R.anim.button_zoom_in);
        animZoomOut = AnimationUtils.loadAnimation(context,R.anim.button_zoom_out);
        animZoomIn.setAnimationListener(this);
        animZoomOut.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

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
                                        }
                                    }
                                });
                                codesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        adapter = new SongListAdapter((AppCompatActivity) getActivity(), mSongs, false, false);
                                        adapter.isOnline = true;
                                        mRecyclerView.setAdapter(adapter);
                                        MusicPlayer.setSongs(mSongs);
                                        mLayoutMain.getParent().bringChildToFront(mLayoutScreenLoad);
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
        switch (v.getId())
        {
            case R.id.btnVieSong :
                if(!collectionPath.equals("Vie")) {
                    collectionPath = "Vie";
                    mSongs.clear();
                    btnVie.getParent().bringChildToFront(v);
                    btnVie.startAnimation(animZoomIn);
                    btnEng.startAnimation(animZoomOut);
                    new getDatabase().execute("");
                }
                break;
            case R.id.btnEngSong:
                if(!collectionPath.equals("Eng")){
                    collectionPath = "Eng";
                    mSongs.clear();
                    btnVie.startAnimation(animZoomOut);
                    btnEng.getParent().bringChildToFront(v);
                    btnEng.startAnimation(animZoomIn);
                    new getDatabase().execute("");
                }
                break;
        }
    }
}
