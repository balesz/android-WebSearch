package net.solutinno.websearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import net.solutinno.websearch.actions.DetailActions;

public class DetailFragment extends SherlockFragment implements DetailActions
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) DeleteEngine(null);
        else if (item.getItemId() == R.id.action_cancel) CancelEngine(null);
        else if (item.getItemId() == R.id.action_save) SaveEngine(null);
        return true;
    }

    @Override
    public void DeleteEngine(String id) {
        Toast.makeText(getSherlockActivity(), "Delete", Toast.LENGTH_LONG);
    }

    @Override
    public void CancelEngine(String id) {
        Toast.makeText(getSherlockActivity(), "Cancel", Toast.LENGTH_LONG);
    }

    @Override
    public void SaveEngine(String id) {
        Toast.makeText(getSherlockActivity(), "Save", Toast.LENGTH_LONG);
    }
}
