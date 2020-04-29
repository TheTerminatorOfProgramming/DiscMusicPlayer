package com.ttop.discmusicplayer.ui.fragments.mainactivity.library.pager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.adapter.album.AlbumAdapter;
import com.ttop.discmusicplayer.interfaces.LoaderIds;
import com.ttop.discmusicplayer.loader.AlbumLoader;
import com.ttop.discmusicplayer.misc.WrappedAsyncTaskLoader;
import com.ttop.discmusicplayer.model.Album;
import com.ttop.discmusicplayer.util.PreferenceUtil;

import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AlbumsFragment extends AbsLibraryPagerRecyclerViewCustomGridSizeFragment<AlbumAdapter, GridLayoutManager> implements LoaderManager.LoaderCallbacks<ArrayList<Album>> {

    private static final int LOADER_ID = LoaderIds.ALBUMS_FRAGMENT;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected GridLayoutManager createLayoutManager() {
        return new GridLayoutManager(getActivity(), getGridSize());
    }

    @NonNull
    @Override
    protected AlbumAdapter createAdapter() {
        int itemLayoutRes = getItemLayoutRes();
        notifyLayoutResChanged(itemLayoutRes);
        ArrayList<Album> dataSet = getAdapter() == null ? new ArrayList<>() : getAdapter().getDataSet();
        return new AlbumAdapter(
                getLibraryFragment().getMainActivity(),
                dataSet,
                itemLayoutRes,
                loadUsePalette(),
                getLibraryFragment());
    }

    @Override
    protected int getEmptyMessage() {
        return R.string.no_albums;
    }

    @Override
    protected String loadSortOrder() {
        return PreferenceUtil.getInstance().getAlbumSortOrder();
    }

    @Override
    protected void saveSortOrder(String sortOrder) {
        PreferenceUtil.getInstance().setAlbumSortOrder(sortOrder);
    }

    @Override
    protected void setSortOrder(String sortOrder) {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean loadUsePalette() {
        return PreferenceUtil.getInstance().albumColoredFooters();
    }

    @Override
    protected void setUsePalette(boolean usePalette) {
        getAdapter().usePalette(usePalette);
    }

    @Override
    protected void setGridSize(int gridSize) {
        getLayoutManager().setSpanCount(gridSize);
        getAdapter().notifyDataSetChanged();
    }

    @Override
    protected int loadGridSize() {
        return PreferenceUtil.getInstance().getAlbumGridSize(getActivity());
    }

    @Override
    protected void saveGridSize(int gridSize) {
        PreferenceUtil.getInstance().setAlbumGridSize(gridSize);
    }

    @Override
    protected int loadGridSizeLand() {
        return PreferenceUtil.getInstance().getAlbumGridSizeLand(getActivity());
    }

    @Override
    protected void saveGridSizeLand(int gridSize) {
        PreferenceUtil.getInstance().setAlbumGridSizeLand(gridSize);
    }

    @Override
    protected void saveUsePalette(boolean usePalette) {
        PreferenceUtil.getInstance().setAlbumColoredFooters(usePalette);
    }

    @Override
    public void onMediaStoreChanged() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<Album>> onCreateLoader(int id, Bundle args) {
        return new AsyncAlbumLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Album>> loader, ArrayList<Album> data) {
        getAdapter().swapDataSet(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Album>> loader) {
        getAdapter().swapDataSet(new ArrayList<>());
    }

    private static class AsyncAlbumLoader extends WrappedAsyncTaskLoader<ArrayList<Album>> {
        public AsyncAlbumLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Album> loadInBackground() {
            return AlbumLoader.getAllAlbums(getContext());
        }
    }
}