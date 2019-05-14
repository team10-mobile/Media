package com.example.mediaplayer.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.mediaplayer.models.Album;

import java.util.ArrayList;

public class ArtistAlbumLoader {


    /**
     * @param context
     * @param artistID, id của nghệ sĩ
     * @return tất cả album của nghệ sĩ theo id của nghệ sĩ
     */
    public static ArrayList<Album> getAlbumsForArtist(Context context, long artistID) {

        ArrayList albumList = new ArrayList();
        Cursor cursor = makeAlbumForArtistCursor(context, artistID);

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {

                    Album album = new Album(cursor.getLong(0), cursor.getString(1), cursor.getString(2), artistID, cursor.getInt(3), cursor.getInt(4));
                    albumList.add(album);
                }
                while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();
        return albumList;
    }

    public static Cursor makeAlbumForArtistCursor(Context context, long artistID) {

        if (artistID == -1)
            return null;

        return context.getContentResolver()
                .query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistID),
                        new String[]{"_id", "album", "artist", "numsongs", "minyear"},
                        null,
                        null,
                        MediaStore.Audio.Albums.FIRST_YEAR);
    }
}
