package net.solutinno.websearch.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.manuelpeinado.multichoiceadapter.extras.actionbarcompat.MultiChoiceSimpleCursorAdapter;

import net.solutinno.websearch.R;

public class ListMultiChoiceAdapter extends MultiChoiceSimpleCursorAdapter {

    ListView mListView;

    public ListMultiChoiceAdapter(Bundle savedInstanceState, Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(savedInstanceState, context, layout, cursor, from, to, flags);
    }

    @Override
    public void setAdapterView(AdapterView<? super BaseAdapter> adapterView) {
        super.setAdapterView(adapterView);
        mListView = (ListView)adapterView;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.list, menu);
        if (mListView != null)
            mListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        super.onDestroyActionMode(mode);
        if (mListView != null)
            mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_list_delete) {
            Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }
}
