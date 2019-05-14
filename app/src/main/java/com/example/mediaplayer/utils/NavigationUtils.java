package com.example.mediaplayer.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;

import com.example.mediaplayer.R;
import com.example.mediaplayer.fragments.AlbumDetailFragment;
import com.example.mediaplayer.fragments.ArtistDetailFragment;

public class NavigationUtils {

    //Di chuyển đến chi tiết album thông qua id của album
    @TargetApi(21)
    public static void navigateToAlbum(Activity context, long albumID, Pair<View, String> transitionViews) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
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
}
