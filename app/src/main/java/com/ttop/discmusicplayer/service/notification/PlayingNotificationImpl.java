package com.ttop.discmusicplayer.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.glide.*;
import com.ttop.discmusicplayer.glide.VinylGlideExtension;
import com.ttop.discmusicplayer.glide.VinylSimpleTarget;
import com.ttop.discmusicplayer.glide.palette.BitmapPaletteWrapper;
import com.ttop.discmusicplayer.model.Song;
import com.ttop.discmusicplayer.service.MusicService;
import com.ttop.discmusicplayer.ui.activities.MainActivity;
import com.ttop.discmusicplayer.util.ImageUtil;
import com.ttop.discmusicplayer.util.MusicUtil;
import com.ttop.discmusicplayer.util.PreferenceUtil;
import com.ttop.discmusicplayer.util.VinylMusicPlayerColorUtil;

public class PlayingNotificationImpl extends PlayingNotification {

    private Target target;

    @Override
    public synchronized void update() {
        stopped = false;

        final Song song = service.getCurrentSong();

        final boolean isPlaying = service.isPlaying();

        final boolean isFavorite = MusicUtil.isFavorite(service, song);

        final RemoteViews notificationLayout = new RemoteViews(service.getPackageName(), R.layout.notification);
        final RemoteViews notificationLayoutBig = new RemoteViews(service.getPackageName(), R.layout.notification_big);

        if (TextUtils.isEmpty(song.title) && TextUtils.isEmpty(song.artistName)) {
            notificationLayout.setViewVisibility(R.id.media_titles, View.INVISIBLE);
        } else {
            notificationLayout.setViewVisibility(R.id.media_titles, View.VISIBLE);
            notificationLayout.setTextViewText(R.id.title, song.title);
            notificationLayout.setTextViewText(R.id.text, song.artistName);
        }

        if (TextUtils.isEmpty(song.title) && TextUtils.isEmpty(song.artistName) && TextUtils.isEmpty(song.albumName)) {
            notificationLayoutBig.setViewVisibility(R.id.media_titles, View.INVISIBLE);
        } else {
            notificationLayoutBig.setViewVisibility(R.id.media_titles, View.VISIBLE);
            notificationLayoutBig.setTextViewText(R.id.title, song.title);
            notificationLayoutBig.setTextViewText(R.id.text, song.artistName);
            notificationLayoutBig.setTextViewText(R.id.text2, song.albumName);
        }

        linkButtons(notificationLayout, notificationLayoutBig);

        Intent action = new Intent(service, MainActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent clickIntent = PendingIntent.getActivity(service, 0, action, 0);
        final PendingIntent deleteIntent = buildPendingIntent(service, MusicService.ACTION_QUIT, null);

        final Notification notification = new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(clickIntent)
                .setDeleteIntent(deleteIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(notificationLayout)
                .setCustomBigContentView(notificationLayoutBig)
                .setOngoing(isPlaying)
                .build();

        final int bigNotificationImageSize = service.getResources().getDimensionPixelSize(R.dimen.notification_big_image_size);
        service.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (target != null) {
                    GlideApp.with(service).clear(target);
                }
                target = GlideApp.with(service)
                        .asBitmapPalette()
                        .load(VinylGlideExtension.getSongModel(song))
                        .transition(VinylGlideExtension.getDefaultTransition())
                        .songOptions(song)
                        .into(new VinylSimpleTarget<BitmapPaletteWrapper>(bigNotificationImageSize, bigNotificationImageSize) {
                            @Override
                            public void onResourceReady(@NonNull BitmapPaletteWrapper resource, Transition<? super BitmapPaletteWrapper> glideAnimation) {
                                update(resource.getBitmap(), VinylMusicPlayerColorUtil.getColor(resource.getPalette(), Color.TRANSPARENT));
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                update(null, Color.WHITE);
                            }

                            private void update(@Nullable Bitmap bitmap, int bgColor) {
                                if (bitmap != null) {
                                    notificationLayout.setImageViewBitmap(R.id.image, bitmap);
                                    notificationLayoutBig.setImageViewBitmap(R.id.image, bitmap);
                                } else {
                                    notificationLayout.setImageViewResource(R.id.image, R.drawable.default_album_art);
                                    notificationLayoutBig.setImageViewResource(R.id.image, R.drawable.default_album_art);
                                }

                                if (!PreferenceUtil.getInstance().coloredNotification()) {
                                    bgColor = Color.WHITE;
                                }
                                setBackgroundColor(bgColor);
                                setNotificationContent(ColorUtil.isColorLight(bgColor));

                                if (stopped)
                                    return; // notification has been stopped before loading was finished
                                updateNotifyModeAndPostNotification(notification);
                            }

                            private void setBackgroundColor(int color) {
                                notificationLayout.setInt(R.id.root, "setBackgroundColor", color);
                                notificationLayoutBig.setInt(R.id.root, "setBackgroundColor", color);
                            }

                            private void setNotificationContent(boolean dark) {
                                int primary = MaterialValueHelper.getPrimaryTextColor(service, dark);
                                int secondary = MaterialValueHelper.getSecondaryTextColor(service, dark);

                                Bitmap prev = ImageUtil.createBitmap(ImageUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_previous_white_24dp, primary), 1.5f);
                                Bitmap next = ImageUtil.createBitmap(ImageUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_next_white_24dp, primary), 1.5f);
                                Bitmap playPause = ImageUtil.createBitmap(ImageUtil.getTintedVectorDrawable(service, isPlaying ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp, primary), 1.5f);
                                Bitmap fav = ImageUtil.createBitmap(ImageUtil.getTintedVectorDrawable(service, isFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp, primary), 1.5f);


                                notificationLayout.setTextColor(R.id.title, primary);
                                notificationLayout.setTextColor(R.id.text, secondary);
                                notificationLayout.setImageViewBitmap(R.id.action_prev, prev);
                                notificationLayout.setImageViewBitmap(R.id.action_next, next);
                                notificationLayout.setImageViewBitmap(R.id.action_play_pause, playPause);
                                notificationLayout.setImageViewBitmap(R.id.action_fav, fav);

                                notificationLayoutBig.setTextColor(R.id.title, primary);
                                notificationLayoutBig.setTextColor(R.id.text, secondary);
                                notificationLayoutBig.setTextColor(R.id.text2, secondary);
                                notificationLayoutBig.setImageViewBitmap(R.id.action_prev, prev);
                                notificationLayoutBig.setImageViewBitmap(R.id.action_next, next);
                                notificationLayoutBig.setImageViewBitmap(R.id.action_play_pause, playPause);
                                notificationLayoutBig.setImageViewBitmap(R.id.action_fav, fav);
                            }
                        });
            }
        });
    }

    private void linkButtons(final RemoteViews notificationLayout, final RemoteViews notificationLayoutBig) {
        PendingIntent pendingIntent;

        final ComponentName serviceName = new ComponentName(service, MusicService.class);

        // Previous track
        pendingIntent = buildPendingIntent(service, MusicService.ACTION_REWIND, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_prev, pendingIntent);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_prev, pendingIntent);

        // Play and pause
        pendingIntent = buildPendingIntent(service, MusicService.ACTION_TOGGLE_PAUSE, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent);

        // Next track
        pendingIntent = buildPendingIntent(service, MusicService.ACTION_SKIP, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_next, pendingIntent);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_next, pendingIntent);

        // Favorite
        pendingIntent = buildPendingIntent(service, MusicService.TOGGLE_FAVORITE, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_fav, pendingIntent);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_fav, pendingIntent);
    }

    private PendingIntent buildPendingIntent(Context context, final String action, final ComponentName serviceName) {
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(context, 0, intent, 0);
    }

}