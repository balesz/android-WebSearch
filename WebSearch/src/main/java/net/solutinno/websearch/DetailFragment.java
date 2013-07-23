package net.solutinno.websearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;

import java.util.UUID;

public class DetailFragment extends SherlockFragment
{
    TextView mFieldName;
    TextView mFieldUrl;
    TextView mFieldImageUrl;
    ImageView mFieldImage;
    TextView mFieldDescription;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFieldName = (TextView) getView().findViewById(R.id.fieldName);
        mFieldUrl = (TextView) getView().findViewById(R.id.fieldUrl);
        mFieldImageUrl = (TextView) getView().findViewById(R.id.fieldImageUrl);
        mFieldImage = (ImageView) getView().findViewById(R.id.fieldImage);
        mFieldDescription = (TextView) getView().findViewById(R.id.fieldDescription);

        setData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) Delete();
        else if (item.getItemId() == R.id.action_save) Save();
        getSherlockActivity().finish();
        return true;
    }

    void setData() {
        String id = getSherlockActivity().getIntent().getStringExtra(SearchEngineCursor.COLUMN_ID);
        if (id != null) {
            SearchEngine engine = DataProvider.getSearchEngine(getSherlockActivity(), UUID.fromString(id));
            mFieldName.setText(engine.name);
            mFieldUrl.setText(engine.url);
            mFieldImageUrl.setText(engine.imageUrl);
            mFieldImage.setImageDrawable(null);
            mFieldDescription.setText(engine.description);
        }
    }

    SearchEngine getData() {
        String id = getSherlockActivity().getIntent().getStringExtra(SearchEngineCursor.COLUMN_ID);
        SearchEngine result = new SearchEngine();
        result.id = id == null ? UUID.randomUUID() : UUID.fromString(id);
        result.name = mFieldName.getText().toString();
        result.url = mFieldUrl.getText().toString();
        result.imageUrl = mFieldImageUrl.getText().toString();
        result.description = mFieldDescription.getText().toString();
        result.image = null;
        return result;
    }

    public void Delete() {
        DataProvider.deleteSearchEngine(getSherlockActivity(), getData());
    }

    public void Save() {
        DataProvider.updateSearchEngine(getSherlockActivity(), getData());
    }
}
