package net.solutinno.websearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.websearch.utils.Helpers;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.UUID;

public class DetailFragment extends SherlockFragment implements View.OnClickListener {
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

        mFieldImage.setOnClickListener(this);

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
        else if (item.getItemId() == R.id.action_cancel) Cancel();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (mFieldImageUrl.getText().toString().isEmpty()) {
            mFieldName.setText("Torrentz SSL");
            mFieldUrl.setText("https://torrentz.eu/search?f={searchTerms}");
            mFieldImageUrl.setText("http://mycroftproject.com/updateos.php/id0/torrentz_secure.ico");
            mFieldDescription.setText("Search dozens of torrent sites");
        }
        else {
            URL url = null;
            try { url = new URL(mFieldImageUrl.getText().toString()); } catch (Exception ex) { }
            new AsyncTask<URL, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(URL... urls) {
                    byte[] data = Helpers.downloadURL(urls[0]);
                    return BitmapFactory.decodeByteArray(data, 0, data.length);
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    mFieldImage.setImageBitmap(bitmap);
                }
            }.execute(url);
        }
    }

    private void setData() {
        String id = getSherlockActivity().getIntent().getStringExtra(SearchEngineCursor.COLUMN_ID);
        if (id != null) {
            SearchEngine engine = DataProvider.getSearchEngine(getSherlockActivity(), UUID.fromString(id));
            mFieldName.setText(engine.name);
            mFieldUrl.setText(engine.url);
            mFieldImageUrl.setText(engine.imageUrl);
            mFieldImage.setImageURI(engine.imageUri);
            mFieldDescription.setText(engine.description);
        }
    }

    private SearchEngine getData() {
        String id = getSherlockActivity().getIntent().getStringExtra(SearchEngineCursor.COLUMN_ID);
        SearchEngine result = new SearchEngine();
        result.id = id == null ? UUID.randomUUID() : UUID.fromString(id);
        result.name = mFieldName.getText().toString();
        result.url = mFieldUrl.getText().toString();
        result.imageUrl = mFieldImageUrl.getText().toString();
        result.description = mFieldDescription.getText().toString();
        result.image = mFieldImage.getDrawable();
        return result;
    }

    private void Delete() {
        DataProvider.deleteSearchEngine(getSherlockActivity(), getData());
        getSherlockActivity().finish();
    }

    private void Save() {
        if (Validate()) {
            DataProvider.updateSearchEngine(getSherlockActivity(), getData());
        }
        getSherlockActivity().finish();
    }

    private void Cancel() {
        getSherlockActivity().finish();
    }

    private boolean Validate() {
        boolean result = !mFieldName.getText().toString().isEmpty();
        result |= !mFieldUrl.getText().toString().isEmpty();
        return  result;
    }

}
