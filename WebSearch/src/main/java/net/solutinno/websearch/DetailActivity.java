package net.solutinno.websearch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.solutinno.websearch.data.SearchEngine;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content = new FrameLayout(this);
        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        content.setId(R.id.layout_detail_container);
        setContentView(content);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(getIntent().getExtras());
        fragment.SetDetailCloseListener(mDetailCloseListener);
        getSupportFragmentManager().beginTransaction()
            .add(R.id.layout_detail_container, fragment,DetailFragment.class.getName())
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getSupportFragmentManager().findFragmentByTag(DetailFragment.class.getName()).onOptionsItemSelected(item);
    }

    DetailFragment.CloseListener mDetailCloseListener = new DetailFragment.CloseListener() {
        @Override
        public void onDetailClosed(int mode, SearchEngine engine) {
            finish();
        }
    };

}
