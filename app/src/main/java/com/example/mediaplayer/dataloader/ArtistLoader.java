package com.example.mediaplayer.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.mediaplayer.models.Artist;

import java.util.ArrayList;
import java.util.List;

public class ArtistLoader {

    public static Artist getArtist(Cursor cursor) {
        Artist artist = new Artist();
        if (cursor != null) {
            if (cursor.moveToFirst())
                artist = new Artist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));
        }
        if (cursor != null)
            cursor.close();
        return artist;
    }

    public static Artist getArtist(Context context, long id) {
        return getArtist(makeArtistCursor(context, "_id=?", new String[]{String.valueOf(id)}));
    }

    public static Cursor makeArtistCursor(Context context, String selection, String[] paramArrayOfString) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{"_id", "artist", "number_of_albums", "number_of_tracks"}, selection, paramArrayOfString, null);
        return cursor;
    }

    public static List<Artist> getAllArtists(Context context) {
        return getArtistsForCursor(makeArtistCursor(context, null, null));
    }

    public static List<Artist> getArtistsForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                arrayList.add(new Artist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }
}
