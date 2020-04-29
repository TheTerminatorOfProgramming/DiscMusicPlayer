package com.ttop.discmusicplayer.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.transition.Transition;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.glide.palette.BitmapPaletteTarget;
import com.ttop.discmusicplayer.glide.palette.BitmapPaletteWrapper;
import com.ttop.discmusicplayer.util.VinylMusicPlayerColorUtil;

public abstract class VinylColoredTarget extends BitmapPaletteTarget {
    public VinylColoredTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        onColorReady(getDefaultFooterColor());
    }

    @Override
    public void onResourceReady(@NonNull BitmapPaletteWrapper resource, Transition<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        onColorReady(VinylMusicPlayerColorUtil.getColor(resource.getPalette(), getDefaultFooterColor()));
    }

    protected int getDefaultFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.defaultFooterColor);
    }

    protected int getAlbumArtistFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.cardBackgroundColor);
    }

    public abstract void onColorReady(int color);
}
