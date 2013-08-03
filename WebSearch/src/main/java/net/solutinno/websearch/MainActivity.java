package net.solutinno.websearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.listener.SelectItemListener;

import java.util.UUID;

public class MainActivity extends ActionBarActivity implements SelectItemListener, DrawerLayout.DrawerListener {

    int mActiveFragment = 0;

    Menu mMainMenu;
    DrawerLayout mDrawerLayout;
    boolean mHasDrawerLayout;

    ListFragment mListFragment;
    DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_list);
        mListFragment.RegisterSelectItemListener(this);

        mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mHasDrawerLayout = mDrawerLayout != null;

        if (mHasDrawerLayout) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerLayout.setDrawerListener(this);
            mListFragment.RegisterSelectItemListener(mDetailFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if (mMainMenu == null) mMainMenu = menu;
        if (mActiveFragment == 0) getMenuInflater().inflate(R.menu.main, mMainMenu);
        else getMenuInflater().inflate(R.menu.detail, mMainMenu);
        return super.onCreateOptionsMenu(mMainMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add:
                Add(); break;
            case R.id.action_save:
                Save(); break;
            case R.id.action_delete:
                Delete(); break;
            case android.R.id.home:
            case R.id.action_cancel:
                Cancel(); break;
        }
        return true;
    }

    @Override
    public void onSelectItem(UUID id) {
        if (mHasDrawerLayout) {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
        else {
            Intent detail = new Intent(this, DetailActivity.class);
            detail.putExtra(SearchEngineCursor.COLUMN_ID, id.toString());
            startActivityForResult(detail, 0);
        }
    }

    @Override
    public void onDrawerSlide(View view, float v) {

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    @Override
    public void onDrawerOpened(View view) {
        mActiveFragment = 1;
        this.onCreateOptionsMenu(mMainMenu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onDrawerClosed(View view) {
        mDetailFragment.ClearFields();
        mActiveFragment = 0;
        this.onCreateOptionsMenu(mMainMenu);
        mListFragment.getLoaderManager().getLoader(0).forceLoad();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    private void Add() {
        if (mHasDrawerLayout) {
            mDetailFragment.onSelectItem(null);
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
        else {
            Intent detail = new Intent(this, DetailActivity.class);
            startActivity(detail);
        }
    }

    private void Save() {
        if (mHasDrawerLayout) mDetailFragment.Save();
        Cancel();
    }

    private void Delete() {
        if (mHasDrawerLayout) mDetailFragment.Delete();
        Cancel();
    }

    private void Cancel() {
        if (mHasDrawerLayout) {
            mDetailFragment.Cancel();
            mDrawerLayout.closeDrawer(GravityCompat.END);
        }
    }

}
