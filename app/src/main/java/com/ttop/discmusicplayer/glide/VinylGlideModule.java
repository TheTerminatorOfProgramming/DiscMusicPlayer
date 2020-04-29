package com.ttop.discmusicplayer.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.ttop.discmusicplayer.glide.artistimage.ArtistImage;
import com.ttop.discmusicplayer.glide.artistimage.ArtistImageLoader;
import com.ttop.discmusicplayer.glide.audiocover.AudioFileCover;
import com.ttop.discmusicplayer.glide.audiocover.AudioFileCoverLoader;
import com.ttop.discmusicplayer.glide.palette.BitmapPaletteTranscoder;
import com.ttop.discmusicplayer.glide.palette.BitmapPaletteWrapper;

import java.io.InputStream;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

@GlideModule
public class VinylGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {
        registry.append(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());
        registry.append(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory(context));
        registry.register(Bitmap.class, BitmapPaletteWrapper.class, new BitmapPaletteTranscoder());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
