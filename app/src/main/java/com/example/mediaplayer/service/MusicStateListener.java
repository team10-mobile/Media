package com.example.mediaplayer.service;

//Lắng nghe các thay đổi phát lại để gửi các fragment bị ràng buộc cho Activity
public interface MusicStateListener {

    void restartLoader();

    void onPlaylistChanged();

    void onMetaChanged();
}
