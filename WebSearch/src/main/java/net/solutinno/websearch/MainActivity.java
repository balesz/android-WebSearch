package net.solutinno.websearch;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;

import java.util.UUID;

public class MainActivity extends ActionBarActivity
{
    DrawerLayout drawerLayout;
    ListFragment listFragment;
    FrameLayout detailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_list);
        listFragment.setSelectItemListener(selectItemListener);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        setupDrawer();
    }

    private void setupDrawer() {
        if (drawerLayout != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawerLayout.setDrawerListener(drawerListener);
            detailContainer = (FrameLayout) findViewById(R.id.layout_detail_container);
            calculateDrawerWidth();
        }
    }

    private void calculateDrawerWidth() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ViewGroup.LayoutParams layoutParams = detailContainer.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = (metrics.widthPixels / 3) * 2;
                detailContainer.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        deleteDetailFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isDetailFragmentShown()) {
            getSupportActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setTitle(R.string.app_name);
            getMenuInflater().inflate(R.menu.main, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add:
                add();
                break;
            case R.id.action_import:
                ImportFragment fragment = ImportFragment.newInstance();
                fragment.setOnImportDialogResult(onImportDialogResult);
                fragment.show(getSupportFragmentManager(), "import");
                break;
            case R.id.action_about:
                AboutFragment about = new AboutFragment();
                about.show(getSupportFragmentManager(), "about");
                break;
            default:
                if (isDetailFragmentShown()) {
                    getDetailFragment().onOptionsItemSelected(item);
                }
                break;
        }
        return true;
    }

    ImportFragment.ImportDialogResult onImportDialogResult = new ImportFragment.ImportDialogResult() {
        @Override
        public void OnDialogResult(int result) {
            if (result == Activity.RESULT_OK) {
                listFragment.getLoaderManager().getLoader(0).forceLoad();
            }
        }
    };

    ListFragment.SelectItemListener selectItemListener = new ListFragment.SelectItemListener() {
        @Override
        public void onSelectItem(UUID id) {
            if (drawerLayout != null) {
                createDetailFragment(id);
                drawerLayout.openDrawer(GravityCompat.END);
            }
            else {
                Intent detail = new Intent(getBaseContext(), DetailActivity.class);
                detail.putExtra(SearchEngineCursor.COLUMN_ID, id.toString());
                startActivityForResult(detail, 0);
                listFragment.clearChoices();
            }
        }
    };

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerOpened(View view) {
            supportInvalidateOptionsMenu();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        @Override
        public void onDrawerClosed(View view) {
            deleteDetailFragment();
            listFragment.getLoaderManager().getLoader(0).forceLoad();
            supportInvalidateOptionsMenu();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            listFragment.clearChoices();
        }
        @Override
        public void onDrawerSlide(View view, float v) {
        }
        @Override
        public void onDrawerStateChanged(int i) {
        }
    };

    DetailFragment.CloseListener detailCloseListener = new DetailFragment.CloseListener() {
        @Override
        public void onDetailClosed(int mode, SearchEngine engine) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    };

    private void add() {
        if (drawerLayout != null) {
            createDetailFragment(null);
            drawerLayout.openDrawer(GravityCompat.END);
        }
        else {
            Intent detail = new Intent(this, DetailActivity.class);
            startActivity(detail);
        }
    }

    private DetailFragment getDetailFragment() {
        if (!isDetailFragmentShown()) return null;
        return (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.class.getName());
    }

    private void createDetailFragment(UUID id) {
        Bundle args = new Bundle();
        if (id != null) args.putString(SearchEngineCursor.COLUMN_ID, id.toString());
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        fragment.SetDetailCloseListener(detailCloseListener);
        getSupportFragmentManager().beginTransaction()
            .add(R.id.layout_detail_container, fragment, DetailFragment.class.getName())
            .commit();
    }

    private void deleteDetailFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DetailFragment.class.getName());
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private boolean isDetailFragmentShown() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END);
    }
}
