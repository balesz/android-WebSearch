package net.solutinno.websearch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends ActionBarActivity {

    DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                mDetailFragment.Delete();
                break;
            case R.id.action_save:
                mDetailFragment.Save();
                break;
            case android.R.id.home:
            case R.id.action_cancel:
                mDetailFragment.Cancel();
                break;
        }
        finish();
        return true;
    }
}
