package net.solutinno.websearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import net.solutinno.websearch.actions.ListActions;

public class ListFragment extends SherlockFragment implements ListActions
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            NewEngine();
            return true;
        }
        return false;
    }

    @Override
    public void NewEngine() {
        Intent detail = new Intent(getSherlockActivity(), DetailActivity.class);
        Bundle params = new Bundle();
        detail.putExtras(params);
        startActivity(detail);
    }

}
