package net.solutinno.websearch;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.websearch.data.SearchEngineLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListFragment extends Fragment {

    ListView mListView;
    SimpleCursorAdapter mAdapter;

    //TODO: Need to free the memory!!
    ArrayList<SelectItemListener> mSelectItemListeners;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_list, container, false);
        if (result != null) {
            mListView = (ListView) result.findViewById(R.id.listView);
            mListView.setOnItemClickListener(mItemClickListener);
        }

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mListView != null) {
            mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, new SearchEngineCursor(), SearchEngineCursor.LIST_FIELDS, SearchEngineCursor.LIST_UI_FIELDS, 1);
            mListView.setAdapter(mAdapter);
            mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int i) {
                    if (view.getId() == R.id.item_icon) {
                        ((ImageView)view).setImageDrawable(null);
                    }
                    return false;
                }
            });
        }

        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    public void RegisterSelectItemListener(SelectItemListener listener) {
        if (mSelectItemListeners == null) mSelectItemListeners = new ArrayList<SelectItemListener>();
        mSelectItemListeners.add(listener);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SearchEngineCursor cursor = (SearchEngineCursor)mAdapter.getCursor();
            UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndex(SearchEngineCursor.COLUMN_ID)));

            if (mSelectItemListeners != null) {
                for (SelectItemListener listener : mSelectItemListeners) listener.onSelectItem(id);
            }
        }
    };

    LoaderManager.LoaderCallbacks<List<SearchEngine>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<SearchEngine>>() {
        @Override
        public Loader<List<SearchEngine>> onCreateLoader(int i, Bundle bundle) {
            return new SearchEngineLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<SearchEngine>> listLoader, List<SearchEngine> searchEngines) {
            if (mAdapter != null) {
                mAdapter.changeCursor(SearchEngineCursor.createBySearchEngineList(searchEngines));
            }
        }

        @Override
        public void onLoaderReset(Loader<List<SearchEngine>> listLoader) {
            if (mAdapter != null) {
                mAdapter.changeCursor(null);
            }
        }
    };

    public static interface SelectItemListener
    {
        void onSelectItem(UUID id);
    }
}

