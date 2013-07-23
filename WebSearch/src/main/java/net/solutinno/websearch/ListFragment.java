package net.solutinno.websearch;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.websearch.data.SearchEngineLoader;

import java.util.List;
import java.util.UUID;

public class ListFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<List<SearchEngine>>, AdapterView.OnItemClickListener {
    ListView mListView;
    SimpleCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mListView != null) {
            mAdapter = new SimpleCursorAdapter(getSherlockActivity(), R.layout.list_item, new SearchEngineCursor(), SearchEngineCursor.FIELDS, SearchEngineCursor.UI_FIELDS, 0);
            mListView.setAdapter(mAdapter);
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_list, container, false);
        if (result != null) {
            mListView = (ListView) result.findViewById(R.id.listView);
            mListView.setOnItemClickListener(this);
        }
        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SearchEngineCursor cursor = (SearchEngineCursor)mAdapter.getCursor();
        UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndex(SearchEngineCursor.COLUMN_ID)));
        Intent detail = new Intent(getSherlockActivity(), DetailActivity.class);
        detail.putExtra(SearchEngineCursor.COLUMN_ID, id.toString());
        startActivityForResult(detail, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent detail = new Intent(getSherlockActivity(), DetailActivity.class);
            startActivity(detail);
        }
        else return false;

        return true;
    }

    @Override
    public Loader<List<SearchEngine>> onCreateLoader(int i, Bundle bundle) {
        return new SearchEngineLoader(getSherlockActivity());
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
            Cursor cursor = mAdapter.getCursor();
            mAdapter.changeCursor(null);
            cursor.close();
        }
    }

}
