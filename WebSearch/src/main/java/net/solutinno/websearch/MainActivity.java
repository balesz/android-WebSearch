package net.solutinno.websearch;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

public class MainActivity extends ActionBarActivity {

    Menu mMainMenu;
    DrawerLayout mDrawerLayout;
    ListFragment mListFragment;
    FrameLayout mDetailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_list);
        mListFragment.setSelectItemListener(mSelectItemListener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerLayout.setDrawerListener(mDrawerListener);

            mDetailContainer = (FrameLayout) findViewById(R.id.layout_detail_container);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ViewGroup.LayoutParams layoutParams = mDetailContainer.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = (metrics.widthPixels / 3) * 2;
                    mDetailContainer.setLayoutParams(layoutParams);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if (mMainMenu == null) mMainMenu = menu;
        if (!isDetailFragmentShown()) {
            getSupportActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setTitle(R.string.app_name);
            getMenuInflater().inflate(R.menu.main, mMainMenu);
        }
        else {
            getMenuInflater().inflate(R.menu.detail, mMainMenu);
        }
        return super.onCreateOptionsMenu(mMainMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add:
                Add();
                break;
            case R.id.action_import:
                ImportFragment fragment = ImportFragment.newInstance();
                fragment.setOnImportDialogResult(mOnImportDialogResult);
                fragment.show(getSupportFragmentManager(), "import");
                break;
            default:
                if (isDetailFragmentShown()) {
                    getDetailFragment().onOptionsItemSelected(item);
                }
                break;
        }
        return true;
    }

    private boolean isDetailFragmentShown() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.END);
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
        fragment.SetDetailCloseListener(mDetailCloseListener);
        getSupportFragmentManager().beginTransaction()
            .add(R.id.layout_detail_container, fragment, DetailFragment.class.getName())
            .commit();
    }

    ImportFragment.ImportDialogResult mOnImportDialogResult = new ImportFragment.ImportDialogResult() {
        @Override
        public void OnDialogResult(int result) {
            if (result == Activity.RESULT_OK) {
                mListFragment.getLoaderManager().getLoader(0).forceLoad();
            }
        }
    };

    ListFragment.SelectItemListener mSelectItemListener = new ListFragment.SelectItemListener() {
        @Override
        public void onSelectItem(UUID id) {
            if (mDrawerLayout != null) {
                createDetailFragment(id);
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
            else {
                Intent detail = new Intent(getBaseContext(), DetailActivity.class);
                detail.putExtra(SearchEngineCursor.COLUMN_ID, id.toString());
                startActivityForResult(detail, 0);
            }
        }
    };

    DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerOpened(View view) {
            onCreateOptionsMenu(mMainMenu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        @Override
        public void onDrawerClosed(View view) {
            getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag(DetailFragment.class.getName()))
                .commit();
            mListFragment.getLoaderManager().getLoader(0).forceLoad();
            onCreateOptionsMenu(mMainMenu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
        @Override
        public void onDrawerSlide(View view, float v) {
        }
        @Override
        public void onDrawerStateChanged(int i) {
        }
    };

    DetailFragment.CloseListener mDetailCloseListener = new DetailFragment.CloseListener() {
        @Override
        public void onDetailClosed(int mode, SearchEngine engine) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        }
    };

    private void Add() {
        if (mDrawerLayout != null) {
            createDetailFragment(null);
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
        else {
            Intent detail = new Intent(this, DetailActivity.class);
            startActivity(detail);
        }
    }
}
