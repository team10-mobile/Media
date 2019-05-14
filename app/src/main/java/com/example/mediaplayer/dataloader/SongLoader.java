package com.example.mediaplayer.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.mediaplayer.models.Song;

import java.util.ArrayList;

public class SongLoader {

    //Nhận thông tin bài hát từ con trỏ được trả về
    public static ArrayList<Song> getSongsForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                int trackNumber = cursor.getInt(5);
                long artistId = cursor.getInt(6);
                long albumId = cursor.getLong(7);

                arrayList.add(new Song(id, albumId, artistId, title, artist, album, duration, trackNumber));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    //Lấy ra tất cả bài hát từ storage của máy
    public static ArrayList<Song> getAllSongs(Context context) {
        return getSongsForCursor(makeSongCursor(context, null, null));
    }

    //Trả về một cursor trở đến một bảng chứa tất cả bài hát trong máy
    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"},
                null, paramArrayOfString, null);
    }

    public static Cursor getCursorData(Context context){
        String[] proj = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE };
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, null);
    }

}
