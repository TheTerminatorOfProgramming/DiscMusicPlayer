package com.ttop.discmusicplayer.util;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.adapter.song.SongAdapter;
import com.ttop.discmusicplayer.glide.*;
import com.ttop.discmusicplayer.glide.VinylColoredTarget;
import com.ttop.discmusicplayer.glide.VinylGlideExtension;
import com.ttop.discmusicplayer.helper.MusicPlayerRemote;
import com.ttop.discmusicplayer.model.Song;

/**
 * @author SC (soncaokim)
 */
public class PlayingSongDecorationUtil {

    public static final int sIconPlaying = R.drawable.ic_notification;

    public static Animation sIconAnimation;
    static {
        sIconAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sIconAnimation.setDuration(ViewUtil.VINYL_MUSIC_PLAYER_ANIM_TIME);
        sIconAnimation.setRepeatCount(Animation.INFINITE);
    }

    public static void decorate(
            @NonNull final SongAdapter songAdapter,
            @NonNull final SongAdapter.ViewHolder holder,
            Song song,
            @NonNull final AppCompatActivity activity)
    {
        PlayingSongDecorationUtil.decorate(holder.title, holder.image, holder.imageText, song, activity, songAdapter.isShowAlbumImage());

        if ((holder.image != null) && songAdapter.isShowAlbumImage()) {
            if (!MusicPlayerRemote.isPlaying(song)) {
                GlideApp.with(activity)
                    .asBitmapPalette()
                    .load(VinylGlideExtension.getSongModel(song))
                    .transition(VinylGlideExtension.getDefaultTransition())
                    .songOptions(song)
                    .into(new VinylColoredTarget(holder.image) {
                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                            super.onLoadCleared(placeholder);
                            songAdapter.setColors(getDefaultFooterColor(), holder);
                        }

                        @Override
                        public void onColorReady(int color) {
                            songAdapter.setColors(songAdapter.isUsePalette() ? color : getDefaultFooterColor(), holder);
                        }
                    });
            }
        }
    }

    public static void decorate(
            @Nullable final TextView title,
            @Nullable final ImageView image,
            @Nullable final TextView imageText,
            Song song,
            @NonNull final AppCompatActivity activity,
            boolean showAlbumImage)
    {
        final boolean isPlaying = MusicPlayerRemote.isPlaying(song);

        if (title != null) {
            title.setTypeface(null, isPlaying ? Typeface.BOLD : Typeface.NORMAL);
        }

        if (image != null) {
            image.setVisibility((isPlaying || showAlbumImage) ? View.VISIBLE : View.GONE);
            final boolean animateIcon = PreferenceUtil.getInstance().animatePlayingSongIcon();

            if (isPlaying) {
                image.setScaleType(ImageView.ScaleType.CENTER);

                final int color = ATHUtil.resolveColor(activity, R.attr.iconColor, ThemeStore.textColorSecondary(activity));
                image.setColorFilter(color, PorterDuff.Mode.SRC_IN);

                final int size = (int)(24 * activity.getResources().getDisplayMetrics().density);

                // Note: No transition for Glide, the animation is explicitly controlled
                GlideApp.with(activity)
                        .asBitmap()
                        .load(sIconPlaying)
                        .override(size)
                        .into(image);

                if (animateIcon) { image.startAnimation(sIconAnimation); }
            }
            else {
                image.clearColorFilter();
                if (animateIcon) { image.clearAnimation(); }
            }
        }

        if (imageText != null) {
            imageText.setVisibility((isPlaying || showAlbumImage) ? View.GONE : View.VISIBLE);
        }
    }

}
