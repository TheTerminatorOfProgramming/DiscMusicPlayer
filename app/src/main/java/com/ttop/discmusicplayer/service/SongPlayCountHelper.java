package com.ttop.discmusicplayer.service;

import com.ttop.discmusicplayer.helper.StopWatch;
import com.ttop.discmusicplayer.model.Song;

class SongPlayCountHelper {
    public static final String TAG = com.ttop.discmusicplayer.service.SongPlayCountHelper.class.getSimpleName();

    private StopWatch stopWatch = new StopWatch();
    private Song song = Song.EMPTY_SONG;

    public Song getSong() {
        return song;
    }

    boolean shouldBumpPlayCount() {
        return song.duration * 0.5d < stopWatch.getElapsedTime();
    }

    void notifySongChanged(Song song) {
        synchronized (this) {
            stopWatch.reset();
            this.song = song;
        }
    }

    void notifyPlayStateChanged(boolean isPlaying) {
        synchronized (this) {
            if (isPlaying) {
                stopWatch.start();
            } else {
                stopWatch.pause();
            }
        }
    }
}
