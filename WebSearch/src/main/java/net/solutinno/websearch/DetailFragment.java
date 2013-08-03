package net.solutinno.websearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.listener.SelectItemListener;
import net.solutinno.util.NetworkHelper;
import net.solutinno.util.StringHelper;

import java.net.URL;
import java.util.UUID;

public class DetailFragment extends Fragment implements View.OnClickListener, SelectItemListener {
    EditText mFieldName;
    EditText mFieldUrl;
    EditText mFieldImageUrl;
    EditText mFieldDescription;
    ImageView mButtonRefreshImage;
    ImageView mButtonAddSearchTerm;

    SearchEngine mEngine;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFieldName = (EditText) getView().findViewById(R.id.fieldName);
        mFieldUrl = (EditText) getView().findViewById(R.id.fieldUrl);
        mFieldImageUrl = (EditText) getView().findViewById(R.id.fieldImageUrl);
        mFieldDescription = (EditText) getView().findViewById(R.id.fieldDescription);

        mButtonRefreshImage = (ImageView) getView().findViewById(R.id.buttonRefreshImage);
        mButtonAddSearchTerm = (ImageView) getView().findViewById(R.id.buttonAddSearchTerm);

        mButtonRefreshImage.setOnClickListener(this);
        mButtonAddSearchTerm.setOnClickListener(this);

        UUID id = getActivity().getIntent().hasExtra(SearchEngineCursor.COLUMN_ID) ?  UUID.fromString(getActivity().getIntent().getStringExtra(SearchEngineCursor.COLUMN_ID)) : null;
        onSelectItem(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onClick(View view) {

        if (view == mButtonRefreshImage) {
            if (StringHelper.IsNullOrEmpty(mFieldImageUrl.getText())) {
                mFieldName.setText("Torrentz SSL");
                mFieldUrl.setText("https://torrentz.eu/search?f={searchTerms}");
                mFieldImageUrl.setText("http://mycroftproject.com/updateos.php/id0/torrentz_secure.ico");
                mFieldDescription.setText("Search dozens of torrent sites");
            }
            else {
                URL url = null;
                try { url = new URL(StringHelper.GetStringFromCharSequence(mFieldImageUrl.getText())); } catch (Exception ex) { ex.printStackTrace(); }
                new AsyncTask<URL, Integer, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(URL... urls) {
                        byte[] data = NetworkHelper.DownloadURL(urls[0]);
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
        else if (view == mButtonAddSearchTerm) {
            if (getActivity().getCurrentFocus() == mFieldUrl) {
                String text = StringHelper.GetStringFromCharSequence(mFieldUrl.getText());
                int selStart = mFieldUrl.getSelectionStart();
                int selEnd = mFieldUrl.getSelectionEnd();
                text = text.substring(0, selStart) + SearchEngine.SEARCH_TERM + text.substring(selEnd);
                selEnd = selStart + SearchEngine.SEARCH_TERM.length();
                mFieldUrl.setText(text);
                mFieldUrl.setSelection(selEnd);
            }
        }
    }

    public void ClearFields() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        mFieldName.setText("");
        mFieldUrl.setText("");
        mFieldImageUrl.setText("");
        mFieldDescription.setText("");
        mButtonRefreshImage.setImageResource(R.drawable.ic_refresh);
    }

    private void setData() {
        ClearFields();
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
        result.name = StringHelper.GetStringFromCharSequence(mFieldName.getText());
        result.url = StringHelper.GetStringFromCharSequence(mFieldUrl.getText());
        result.imageUrl = StringHelper.GetStringFromCharSequence(mFieldImageUrl.getText());
        result.description = StringHelper.GetStringFromCharSequence(mFieldDescription.getText());
        result.image = mEngine.image;
        return result;
    }

    public void Cancel() {

    }

    public void Save() {
        if (Validate()) {
            DataProvider.updateSearchEngine(getActivity(), getData());
        }
    }

    public void Delete() {
        DataProvider.deleteSearchEngine(getActivity(), getData());
    }

    public boolean Validate() {
        boolean result = !StringHelper.IsNullOrEmpty(mFieldName.getText());
        result |= !StringHelper.IsNullOrEmpty(mFieldUrl.getText());
        return  result;
    }

    @Override
    public void onSelectItem(UUID id) {
        if (id != null) {
            mEngine = DataProvider.getSearchEngine(getActivity(), id);
        }
        else mEngine = new SearchEngine();

        setData();
    }
}
