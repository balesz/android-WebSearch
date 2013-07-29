package net.solutinno.websearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.websearch.utils.Helpers;

import java.net.URL;
import java.util.UUID;

public class DetailFragment extends Fragment implements View.OnClickListener {
    TextView mFieldName;
    TextView mFieldUrl;
    TextView mFieldImageUrl;
    TextView mFieldDescription;
    ImageView mButtonRefreshImage;
    ImageView mButtonAddSearchTerm;

    SearchEngine mEngine;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String id = getActivity().getIntent().getStringExtra(SearchEngineCursor.COLUMN_ID);
        if (id != null) {
            mEngine = DataProvider.getSearchEngine(getActivity(), UUID.fromString(id));
        }
        else mEngine = new SearchEngine();

        mFieldName = (TextView) getView().findViewById(R.id.fieldName);
        mFieldUrl = (TextView) getView().findViewById(R.id.fieldUrl);
        mFieldImageUrl = (TextView) getView().findViewById(R.id.fieldImageUrl);
        mFieldDescription = (TextView) getView().findViewById(R.id.fieldDescription);

        mButtonRefreshImage = (ImageView) getView().findViewById(R.id.buttonRefreshImage);
        mButtonAddSearchTerm = (ImageView) getView().findViewById(R.id.buttonAddSearchTerm);

        mButtonRefreshImage.setOnClickListener(this);
        mButtonAddSearchTerm.setOnClickListener(this);

        setData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                Delete(); break;
            case R.id.action_save:
                Save(); break;
            case android.R.id.home:
            case R.id.action_cancel:
                Cancel(); break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        if (view == mButtonRefreshImage) {
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
                        BitmapDrawable icon = new BitmapDrawable(null, Bitmap.createScaledBitmap(bitmap, 48, 48, true));
                        mButtonRefreshImage.setImageBitmap(bitmap);
                        mEngine.image = icon;
                    }
                }.execute(url);
            }
        }
    }

    private void setData() {
        if (mEngine.id != null) {
            mFieldName.setText(mEngine.name);
            mFieldUrl.setText(mEngine.url);
            mFieldImageUrl.setText(mEngine.imageUrl);
            Bitmap bmp = BitmapFactory.decodeFile(mEngine.imageUri.getPath());
            if (bmp != null) mButtonRefreshImage.setImageBitmap(bmp);
            mFieldDescription.setText(mEngine.description);
        }
    }

    private SearchEngine getData() {
        SearchEngine result = new SearchEngine();
        result.id = mEngine.id == null ? UUID.randomUUID() : mEngine.id;
        result.name = mFieldName.getText().toString();
        result.url = mFieldUrl.getText().toString();
        result.imageUrl = mFieldImageUrl.getText().toString();
        result.description = mFieldDescription.getText().toString();
        result.image = mEngine.image;
        return result;
    }

    private void Delete() {
        DataProvider.deleteSearchEngine(getActivity(), getData());
        getActivity().finish();
    }

    private void Save() {
        if (Validate()) {
            DataProvider.updateSearchEngine(getActivity(), getData());
        }
        getActivity().finish();
    }

    private void Cancel() {
        getActivity().finish();
    }

    private boolean Validate() {
        boolean result = !mFieldName.getText().toString().isEmpty();
        result |= !mFieldUrl.getText().toString().isEmpty();
        return  result;
    }

}
