package net.solutinno.websearch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;

import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.Database;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.websearch.data.SearchEngineLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListFragment extends Fragment {

    ListView mListView;
    SimpleCursorAdapter mAdapter;

    ProgressBar mProgressBar;

    //TODO: Need to free the memory!!
    ArrayList<SelectItemListener> mSelectItemListeners;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_list, container, false);
        if (result != null) {
            mProgressBar = (ProgressBar) result.findViewById(R.id.list_progressBar);
            mListView = (ListView) result.findViewById(R.id.list_listView);
            mListView.setOnItemClickListener(mItemClickListener);
        }
        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mListView != null) {
            mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item_list, new SearchEngineCursor(), SearchEngineCursor.LIST_FIELDS, SearchEngineCursor.LIST_UI_FIELDS, 1);
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

        boolean dbIsExists = Database.isExists(getActivity());
        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
        if (!dbIsExists) loadDefaultEngines();
    }

    public void RegisterSelectItemListener(SelectItemListener listener) {
        if (mSelectItemListeners == null) mSelectItemListeners = new ArrayList<SelectItemListener>();
        mSelectItemListeners.add(listener);
    }

    private void loadDefaultEngines() {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    new AsyncTask<Context, Integer, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Context... contexts) {
                            DataProvider.fillDatabase(contexts[0]);
                            return true;
                        }
                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            mProgressBar.setVisibility(View.GONE);
                            getLoaderManager().getLoader(0).forceLoad();
                        }
                    }.execute(getActivity());
                }
            }
        };
        new AlertDialog.Builder(getActivity())
            .setTitle(R.string.dialog_import_defaults_title)
            .setIcon(R.drawable.ic_import_export)
            .setMessage(R.string.dialog_import_defaults_message)
            .setPositiveButton(R.string.caption_yes, clickListener)
            .setNegativeButton(R.string.caption_no, clickListener)
            .show();
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

