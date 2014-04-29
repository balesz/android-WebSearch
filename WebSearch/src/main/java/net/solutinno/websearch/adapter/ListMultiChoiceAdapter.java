package net.solutinno.websearch.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.manuelpeinado.multichoiceadapter.extras.actionbarcompat.MultiChoiceSimpleCursorAdapter;

import net.solutinno.websearch.R;
import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;

import java.util.ArrayList;
import java.util.UUID;

public class ListMultiChoiceAdapter extends MultiChoiceSimpleCursorAdapter {

    ListView mListView;
    Runnable onFinishActionMode;

    public ListMultiChoiceAdapter(Bundle savedInstanceState, Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(savedInstanceState, context, layout, cursor, from, to, flags);
    }

    public void setOnFinishActionMode(Runnable callback) {
        onFinishActionMode = callback;
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
        if (mListView != null) {
            mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_list_delete && getCheckedItemCount() > 0) {

            DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_POSITIVE) {
                        Cursor cursor = getCursor();
                        ArrayList<Long> checkedItems = new ArrayList<Long>(getCheckedItems());
                        for (int pos = 0; pos < getCount(); ++pos) {
                            if (checkedItems.contains(getItemId(pos))) {
                                cursor.moveToPosition(pos);
                                UUID uuid = UUID.fromString(cursor.getString(1));
                                DataProvider.deleteSearchEngine(getContext(), new SearchEngine(uuid));
                            }
                        }
                        finishActionMode();
                        if (onFinishActionMode != null)
                            onFinishActionMode.run();
                    }
                }
            };

            new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_confirmation)
                .setMessage(getCheckedItemCount() == 1 ? R.string.confirmation_delete : R.string.confirmation_delete_multiple)
                .setCancelable(true)
                .setNegativeButton(R.string.caption_no, click)
                .setPositiveButton(R.string.caption_yes, click)
                .show();

            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }
}
