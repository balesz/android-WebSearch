package net.solutinno.websearch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.solutinno.websearch.data.SearchEngine;

public class DetailActivity extends ActionBarActivity {

    DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        mDetailFragment.SetDetailController(mDetailController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDetailFragment.onOptionsItemSelected(item);
    }

    DetailFragment.DetailController mDetailController = new DetailFragment.DetailController() {
        @Override
        public void OnDetailFinish(int mode, SearchEngine engine) {
            switch (mode)
            {
                case DetailFragment.MODE_CANCEL:
                    finish();
                    break;
                case DetailFragment.MODE_UPDATE:
                    finish();
                    break;
                case DetailFragment.MODE_DELETE:
                    finish();
                    break;
            }
        }
    };

}
