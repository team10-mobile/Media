package com.example.mediaplayer.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activities.MainActivity;
import com.example.mediaplayer.activities.PlaylistDetailActivity;
import com.example.mediaplayer.fragments.AlbumDetailFragment;
import com.example.mediaplayer.fragments.ArtistDetailFragment;

import java.util.ArrayList;

public class NavigationUtils {

    //Di chuyển đến chi tiết album thông qua id của album
    @TargetApi(21)
    public static void navigateToAlbum(Activity context, long albumID, Pair<View, String> transitionViews) {

        FragmentTransaction transaction =
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        transaction.setCustomAnimations(R.anim.activity_fade_in,
                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = AlbumDetailFragment.newInstance(albumID, false, null);

        transaction.hide(((AppCompatActivity) context).
                getSupportFragmentManager().findFragmentById(R.id.frame_music_contains));
        transaction.add(R.id.frame_music_contains, fragment);
        transaction.addToBackStack(null).commit();
    }


    /**
     * Mở một fragment mới chứa chi tiết thông tin về nghệ sĩ
     */
    @TargetApi(21)
    public static void navigateToArtist(Activity context, long artistID,
                                        Pair<View, String> transitionViews) {
        FragmentTransaction transaction =
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        transaction.setCustomAnimations(R.anim.activity_fade_in,
                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);

        fragment = ArtistDetailFragment.newInstance(artistID, false, null);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_music_contains));
        transaction.add(R.id.frame_music_contains, fragment);
        transaction.addToBackStack(null).commit();

    }

    @TargetApi(21)
    public static void navigateToPlaylistDetail(Activity context, String action, long firstAlbumID, String playlistName, int foregroundcolor, long playlistID, ArrayList<Pair> transitionViews) {
        final Intent intent = new Intent(context, PlaylistDetailActivity.class);
        intent.setAction(action);
        intent.putExtra(Constants.PLAYLIST_ID, playlistID);
        intent.putExtra(Constants.PLAYLIST_FOREGROUND_COLOR, foregroundcolor);
        intent.putExtra(Constants.ALBUM_ID, firstAlbumID);
        intent.putExtra(Constants.PLAYLIST_NAME, playlistName);
        intent.putExtra(Constants.ACTIVITY_TRANSITION, transitionViews != null);

        if (transitionViews != null && MusicUtils.isLollipop()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, transitionViews.get(0), transitionViews.get(1), transitionViews.get(2));
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST, options.toBundle());
        } else {
            context.startActivityForResult(intent, Constants.ACTION_DELETE_PLAYLIST);
        }
    }


    public static void goToArtist(Context context, long artistId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID, artistId);
        context.startActivity(intent);
    }

}
