package com.ttop.discmusicplayer.appshortcuts.shortcuttype;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.appshortcuts.AppShortcutIconGenerator;
import com.ttop.discmusicplayer.appshortcuts.AppShortcutLauncherActivity;
import com.ttop.discmusicplayer.helper.MusicPlayerRemote;
import com.ttop.discmusicplayer.loader.SongLoader;
import com.ttop.discmusicplayer.model.Song;

import java.util.ArrayList;

/**
 * @author Adrian Campos
 */
@TargetApi(Build.VERSION_CODES.N_MR1)
public final class ShuffleAllShortcutType extends BaseShortcutType {
    Context context;
    public ShuffleAllShortcutType(Context context) {
        super(context);
        this.context = context;
    }

    public static String getId() {
        return ID_PREFIX + "shuffle_all";
    }

    public ShortcutInfo getShortcutInfo() {
        return new ShortcutInfo.Builder(context, getId())
                .setShortLabel(context.getString(R.string.app_shortcut_shuffle_all_short))
                .setLongLabel(context.getString(R.string.action_shuffle_all))
                .setIcon(AppShortcutIconGenerator.generateThemedIcon(context, R.drawable.ic_app_shortcut_shuffle_all))
                .setIntent(getPlaySongsIntent(AppShortcutLauncherActivity.SHORTCUT_TYPE_SHUFFLE_ALL))
                .build();
    }
}
