package com.example.mediaplayer.ClassLayer;

import android.net.Uri;

public class Songs {

    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private int mFile;
    private Uri mUri;
    public Songs(String mTitle, String mArtist, String mAlbum, int mFile) {
        this.mTitle = mTitle;
        this.mArtist = mArtist;
        this.mAlbum = mAlbum;
        this.mFile = mFile;
    }

    public Songs(String title, String artist, Uri uri)
    {
        this.mTitle = title;
        this.mArtist = artist;
        this.mUri = uri;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public int getmFile() {
        return mFile;
    }

    public void setmFile(int mFile) {
        this.mFile = mFile;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
    }

}
