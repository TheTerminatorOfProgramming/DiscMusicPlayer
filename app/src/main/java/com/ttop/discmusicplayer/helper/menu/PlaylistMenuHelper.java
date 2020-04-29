package com.ttop.discmusicplayer.helper.menu;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ttop.discmusicplayer.App;
import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.dialogs.AddToPlaylistDialog;
import com.ttop.discmusicplayer.dialogs.DeletePlaylistDialog;
import com.ttop.discmusicplayer.dialogs.RenamePlaylistDialog;
import com.ttop.discmusicplayer.helper.MusicPlayerRemote;
import com.ttop.discmusicplayer.misc.WeakContextAsyncTask;
import com.ttop.discmusicplayer.model.Playlist;
import com.ttop.discmusicplayer.util.PlaylistsUtil;

import java.io.IOException;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class PlaylistMenuHelper {
    public static boolean handleMenuClick(@NonNull AppCompatActivity activity, @NonNull final Playlist playlist, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                MusicPlayerRemote.openQueue(playlist.getSongs(activity), 0, true);
                return true;
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(playlist.getSongs(activity));
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(playlist.getSongs(activity));
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(playlist.getSongs(activity)).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_rename_playlist:
                RenamePlaylistDialog.create(playlist.id).show(activity.getSupportFragmentManager(), "RENAME_PLAYLIST");
                return true;
            case R.id.action_delete_playlist:
                DeletePlaylistDialog.create(playlist).show(activity.getSupportFragmentManager(), "DELETE_PLAYLIST");
                return true;
            case R.id.action_save_playlist:
                new SavePlaylistAsyncTask(activity).execute(playlist);
                return true;
        }
        return false;
    }

    private static class SavePlaylistAsyncTask extends WeakContextAsyncTask<Playlist, String, String> {
        public SavePlaylistAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(Playlist... params) {
            try {
                return String.format(App.getInstance().getApplicationContext().getString(R.string.saved_playlist_to), PlaylistsUtil.savePlaylist(App.getInstance().getApplicationContext(), params[0]));
            } catch (IOException e) {
                e.printStackTrace();
                return String.format(App.getInstance().getApplicationContext().getString(R.string.failed_to_save_playlist), e);
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show();
            }
        }
    }
}
