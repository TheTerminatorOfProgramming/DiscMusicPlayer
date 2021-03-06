package com.ttop.discmusicplayer.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.ttop.discmusicplayer.model.Song;
import com.ttop.discmusicplayer.util.PreferenceUtil;

import java.util.ArrayList;

public class LastAddedLoader {

    @NonNull
    public static ArrayList<Song> getLastAddedSongs(@NonNull Context context) {
        return SongLoader.getSongs(makeLastAddedCursor(context));
    }

    public static Cursor makeLastAddedCursor(@NonNull final Context context) {
        long cutoff = PreferenceUtil.getInstance().getLastAddedCutoffTimeSecs();

        return SongLoader.makeSongCursor(
                context,
                MediaStore.Audio.Media.DATE_ADDED + ">?",
                new String[]{String.valueOf(cutoff)},
                MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }
}
