package com.ttop.discmusicplayer.adapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.glide.*;
import com.ttop.discmusicplayer.glide.VinylColoredTarget;
import com.ttop.discmusicplayer.glide.VinylGlideExtension;
import com.ttop.discmusicplayer.misc.CustomFragmentStatePagerAdapter;
import com.ttop.discmusicplayer.model.Song;
import com.ttop.discmusicplayer.util.PreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AlbumCoverPagerAdapter extends CustomFragmentStatePagerAdapter {

    private ArrayList<Song> dataSet;

    private AlbumCoverFragment.ColorReceiver currentColorReceiver;
    private int currentColorReceiverPosition = -1;

    public AlbumCoverPagerAdapter(FragmentManager fm, ArrayList<Song> dataSet) {
        super(fm);
        this.dataSet = dataSet;
    }

    @Override
    public Fragment getItem(final int position) {
        return AlbumCoverFragment.newInstance(dataSet.get(position));
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        Object o = super.instantiateItem(container, position);
        if (currentColorReceiver != null && currentColorReceiverPosition == position) {
            receiveColor(currentColorReceiver, currentColorReceiverPosition);
        }
        return o;
    }

    /**
     * Only the latest passed {@link AlbumCoverFragment.ColorReceiver} is guaranteed to receive a response
     */
    public void receiveColor(AlbumCoverFragment.ColorReceiver colorReceiver, int position) {
        AlbumCoverFragment fragment = (AlbumCoverFragment) getFragment(position);
        if (fragment != null) {
            currentColorReceiver = null;
            currentColorReceiverPosition = -1;
            fragment.receiveColor(colorReceiver, position);
        } else {
            currentColorReceiver = colorReceiver;
            currentColorReceiverPosition = position;
        }
    }

    public static class AlbumCoverFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String SONG_ARG = "song";

        private Unbinder unbinder;

        @BindView(R.id.player_image)
        ImageView albumCover;

        private boolean isColorReady;
        private int color;
        private Song song;
        private ColorReceiver colorReceiver;
        private int request;

        public static AlbumCoverFragment newInstance(final Song song) {
            AlbumCoverFragment frag = new AlbumCoverFragment();
            final Bundle args = new Bundle();
            args.putParcelable(SONG_ARG, song);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            song = getArguments().getParcelable(SONG_ARG);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_album_cover, container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            forceSquareAlbumCover(false);
            // TODO
//            forceSquareAlbumCover(PreferenceUtil.getInstance().forceSquareAlbumCover());
            PreferenceUtil.getInstance().registerOnSharedPreferenceChangedListener(this);
            loadAlbumCover();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PreferenceUtil.getInstance().unregisterOnSharedPreferenceChangedListener(this);
            unbinder.unbind();
            colorReceiver = null;
        }

        private void loadAlbumCover() {
            GlideApp.with(this)
                    .asBitmapPalette()
                    .load(VinylGlideExtension.getSongModel(song))
                    .transition(VinylGlideExtension.getDefaultTransition())
                    .songOptions(song)
                    .into(new VinylColoredTarget(albumCover) {
                        @Override
                        public void onColorReady(int color) {
                            setColor(color);
                        }
                    });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferenceUtil.FORCE_SQUARE_ALBUM_COVER:
                    // TODO
//                    forceSquareAlbumCover(PreferenceUtil.getInstance().forceSquareAlbumCover());
                    break;
            }
        }

        public void forceSquareAlbumCover(boolean forceSquareAlbumCover) {
            albumCover.setScaleType(forceSquareAlbumCover ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
        }

        private void setColor(int color) {
            this.color = color;
            isColorReady = true;
            if (colorReceiver != null) {
                colorReceiver.onColorReady(color, request);
                colorReceiver = null;
            }
        }

        public void receiveColor(ColorReceiver colorReceiver, int request) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request);
            } else {
                this.colorReceiver = colorReceiver;
                this.request = request;
            }
        }

        public interface ColorReceiver {
            void onColorReady(int color, int request);
        }
    }
}

