package com.ttop.discmusicplayer.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.ttop.discmusicplayer.App;
import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.glide.artistimage.ArtistImage;
import com.ttop.discmusicplayer.glide.audiocover.AudioFileCover;
import com.ttop.discmusicplayer.glide.palette.BitmapPaletteWrapper;
import com.ttop.discmusicplayer.model.Artist;
import com.ttop.discmusicplayer.model.Song;
import com.ttop.discmusicplayer.util.ArtistSignatureUtil;
import com.ttop.discmusicplayer.util.CustomArtistImageUtil;
import com.ttop.discmusicplayer.util.MusicUtil;
import com.ttop.discmusicplayer.util.PreferenceUtil;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

@GlideExtension
public final class VinylGlideExtension {
    private VinylGlideExtension() {
    }

    @GlideType(BitmapPaletteWrapper.class)
    public static void asBitmapPalette(RequestBuilder<BitmapPaletteWrapper> requestBuilder) {
    }

    @GlideOption
    @NonNull
    public static RequestOptions artistOptions(RequestOptions requestOptions, Artist artist) {
        return requestOptions
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.default_artist_image)
                .placeholder(R.drawable.default_artist_image)
                .priority(Priority.LOW)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .signature(createSignature(artist));
    }

    @GlideOption
    @NonNull
    public static RequestOptions songOptions(RequestOptions requestOptions, Song song) {
        return requestOptions
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_album_art)
                .placeholder(R.drawable.default_album_art)
                .signature(createSignature(song));
    }

    public static Key createSignature(Artist artist) {
        return ArtistSignatureUtil.getInstance().getArtistSignature(artist.getName());
    }

    public static Key createSignature(Song song) {
        return new MediaStoreSignature("", song.dateModified, 0);
    }

    public static Object getArtistModel(Artist artist) {
        return getArtistModel(artist, CustomArtistImageUtil.getInstance(App.getInstance()).hasCustomArtistImage(artist), false);
    }

    public static Object getArtistModel(Artist artist, boolean forceDownload) {
        return getArtistModel(artist, CustomArtistImageUtil.getInstance(App.getInstance()).hasCustomArtistImage(artist), forceDownload);
    }

    public static Object getArtistModel(Artist artist, boolean hasCustomImage, boolean forceDownload) {
        if (!hasCustomImage) {
            return new ArtistImage(artist.getName(), forceDownload);
        } else {
            return CustomArtistImageUtil.getFile(artist);
        }
    }

    public static Object getSongModel(Song song) {
        return getSongModel(song, PreferenceUtil.getInstance().ignoreMediaStoreArtwork());
    }

    public static Object getSongModel(Song song, boolean ignoreMediaStore) {
        if (ignoreMediaStore) {
            return new AudioFileCover(song.data);
        } else {
            return MusicUtil.getMediaStoreAlbumCoverUri(song.albumId);
        }
    }

    public static <TranscodeType> GenericTransitionOptions<TranscodeType> getDefaultTransition() {
        return new GenericTransitionOptions<TranscodeType>().transition(android.R.anim.fade_in);
    }
}
