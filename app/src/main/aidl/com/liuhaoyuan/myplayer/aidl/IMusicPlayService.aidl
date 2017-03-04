// IMusicPlayService.aidl
package com.liuhaoyuan.myplayer.aidl;
import com.liuhaoyuan.myplayer.aidl.Song;
// Declare any non-default types here with import statements

interface IMusicPlayService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void openAudio(int position);

    void play();

    void pause();

    void seekTo(int position);

    void setPlayMode(int mode);

    int getPlayMode();

    void previous();

    void next();

    String getTitle();

    String getArtist();

    String getUrl();

    int getDuration();

    int getCurrentPosition();

    int getCurrentProgress();

    boolean isPlaying();

    List<Song> getSongList();
}
