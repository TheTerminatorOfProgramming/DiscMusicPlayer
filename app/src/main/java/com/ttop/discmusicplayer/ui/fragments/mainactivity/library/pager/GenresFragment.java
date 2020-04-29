package com.ttop.discmusicplayer.ui.fragments.mainactivity.library.pager;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ttop.discmusicplayer.R;
import com.ttop.discmusicplayer.adapter.GenreAdapter;
import com.ttop.discmusicplayer.interfaces.LoaderIds;
import com.ttop.discmusicplayer.loader.GenreLoader;
import com.ttop.discmusicplayer.misc.WrappedAsyncTaskLoader;
import com.ttop.discmusicplayer.model.Genre;

import java.util.ArrayList;

public class GenresFragment extends AbsLibraryPagerRecyclerViewFragment<GenreAdapter, LinearLayoutManager> implements LoaderManager.LoaderCallbacks<ArrayList<Genre>> {

    private static final int LOADER_ID = LoaderIds.GENRES_FRAGMENT;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    protected LinearLayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @NonNull
    @Override
    protected GenreAdapter createAdapter() {
        ArrayList<Genre> dataSet = getAdapter() == null ? new ArrayList<>() : getAdapter().getDataSet();
        return new GenreAdapter(getLibraryFragment().getMainActivity(), dataSet, R.layout.item_list_no_image);
    }

    @Override
    protected int getEmptyMessage() {
        return R.string.no_genres;
    }

    @Override
    public void onMediaStoreChanged() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    @NonNull
    public Loader<ArrayList<Genre>> onCreateLoader(int id, Bundle args) {
        return new GenresFragment.AsyncGenreLoader(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Genre>> loader, ArrayList<Genre> data) {
        getAdapter().swapDataSet(data);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.action_shuffle_all);
        if(item!=null)
            item.setVisible(false);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Genre>> loader) {
        getAdapter().swapDataSet(new ArrayList<>());
    }

    private static class AsyncGenreLoader extends WrappedAsyncTaskLoader<ArrayList<Genre>> {
        public AsyncGenreLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Genre> loadInBackground() {
            return GenreLoader.getAllGenres(getContext());
        }
    }
}
