package net.solutinno.websearch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;

import java.util.UUID;

public class ShareActivity extends Activity implements AdapterView.OnItemClickListener {
    GridView mShareGrid;
    String mSearchTerm;
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mSearchTerm = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        mShareGrid = (GridView) findViewById(R.id.share_grid);
        mShareGrid.setOnItemClickListener(this);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
        width = width < height ? width : height;

        ViewGroup.LayoutParams params = mShareGrid.getLayoutParams();
        params.width = params.height = (width/4)*3;
        mShareGrid.setLayoutParams(params);

        SearchEngineCursor cursor = SearchEngineCursor.createBySearchEngineList(DataProvider.getSearchEngines(this));
        mAdapter = new SimpleCursorAdapter(this, R.layout.grid_item, cursor, SearchEngineCursor.GRID_FIELDS, SearchEngineCursor.GRID_UI_FIELDS, 0);
        mShareGrid.setAdapter(mAdapter);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String id = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(SearchEngineCursor.COLUMN_ID));
        if (id == null) return;
        SearchEngine engine = DataProvider.getSearchEngine(this, UUID.fromString(id));
        String url = engine.url.replace(SearchEngine.SEARCH_TERM, mSearchTerm);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        finish();
    }
}
