package net.solutinno.websearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.solutinno.util.DrawableHelper;
import net.solutinno.util.UrlHelper;
import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;
import net.solutinno.util.NetworkHelper;
import net.solutinno.util.StringHelper;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.UUID;

public class DetailFragment extends Fragment implements ListFragment.SelectItemListener {

    private final int ICON_WIDTH = 48;
    private final int ICON_HEIGHT = 48;

    EditText mFieldName;
    EditText mFieldUrl;
    EditText mFieldImageUrl;
    EditText mFieldDescription;
    ImageView mButtonAddSearchTerm;
    ImageView mButtonLoadImage;

    ProgressBar mProgressBar;

    SearchEngine mEngine;

    WeakReference<DetailController> mDetailController;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.detail_progressBar);

        mFieldName = (EditText) getView().findViewById(R.id.detail_fieldName);
        mFieldUrl = (EditText) getView().findViewById(R.id.detail_fieldUrl);
        mFieldImageUrl = (EditText) getView().findViewById(R.id.detail_fieldImageUrl);
        mFieldDescription = (EditText) getView().findViewById(R.id.detail_fieldDescription);

        mButtonAddSearchTerm = (ImageView) getView().findViewById(R.id.detail_buttonAddSearchTerm);
        mButtonLoadImage = (ImageView) getView().findViewById(R.id.detail_buttonLoadImage);

        mButtonAddSearchTerm.setOnClickListener(mButtonAddSearchTermClickListener);
        mButtonLoadImage.setOnClickListener(mButtonLoadImageClickListener);

        UUID id = getActivity().getIntent().hasExtra(SearchEngineCursor.COLUMN_ID) ?  UUID.fromString(getActivity().getIntent().getStringExtra(SearchEngineCursor.COLUMN_ID)) : null;
        onSelectItem(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDetailController != null) {
            mDetailController.clear();
            mDetailController = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                Delete();
                break;
            case R.id.action_save:
                Save();
                break;
            case android.R.id.home:
            case R.id.action_cancel:
                Cancel();
                break;
        }
        return true;
    }

    @Override
    public void onSelectItem(UUID id) {
        if (id != null) {
            mEngine = DataProvider.getSearchEngine(getActivity(), id);
        }
        else mEngine = new SearchEngine();

        setData();
    }

    View.OnClickListener mButtonAddSearchTermClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getActivity().getCurrentFocus() != mFieldUrl) return;

            String text = StringHelper.getStringFromCharSequence(mFieldUrl.getText());
            int selStart = mFieldUrl.getSelectionStart();
            int selEnd = mFieldUrl.getSelectionEnd();
            text = text.substring(0, selStart) + SearchEngine.SEARCH_TERM + text.substring(selEnd);
            selEnd = selStart + SearchEngine.SEARCH_TERM.length();
            mFieldUrl.setText(text);
            mFieldUrl.setSelection(selEnd);
        }
    };

    View.OnClickListener mButtonLoadImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = StringHelper.getStringFromCharSequence(mFieldImageUrl.getText());
            if (StringHelper.isNullOrEmpty(url) || !UrlHelper.isUrlValid(url)) {
                notifyValidity(mFieldImageUrl, R.string.error_invalid_url);
                return;
            }
            new AsyncTask<String, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... urls) {
                    try {
                        byte[] data = NetworkHelper.downloadIntoByteArray(new URL(urls[0]));
                        return BitmapFactory.decodeByteArray(data, 0, data.length);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    Toast.makeText(getActivity(), R.string.information_image_download_successfull, Toast.LENGTH_LONG).show();
                    ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
                    BitmapDrawable icon = (BitmapDrawable) DrawableHelper.getDrawableFromBitmap(bitmap, ICON_WIDTH, ICON_HEIGHT);
                    actionBar.setIcon(icon);
                    mEngine.image = icon;
                }
            }.execute(url);
        }
    };


    public void SetDetailController(DetailController controller) {
        mDetailController = new WeakReference<DetailController>(controller);
    }

    public void ClearFields() {
        mFieldName.setText("");
        mFieldUrl.setText("");
        mFieldImageUrl.setText("");
        mFieldDescription.setText("");
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("");
        ((ActionBarActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
    }

    private SearchEngine getData() {
        SearchEngine result = new SearchEngine();
        result.id = mEngine.id == null ? UUID.randomUUID() : mEngine.id;
        result.name = StringHelper.getStringFromCharSequence(mFieldName.getText());
        result.url = StringHelper.getStringFromCharSequence(mFieldUrl.getText());
        result.imageUrl = StringHelper.getStringFromCharSequence(mFieldImageUrl.getText());
        result.description = StringHelper.getStringFromCharSequence(mFieldDescription.getText());
        result.image = mEngine.image;
        return result;
    }

    private void setData() {
        ClearFields();
        if (mEngine.id != null) {
            mFieldName.setText(mEngine.name);
            mFieldUrl.setText(mEngine.url);
            mFieldImageUrl.setText(mEngine.imageUrl);
            mFieldDescription.setText(mEngine.description);
            setImageFromUri(mEngine.imageUri);
        }
    }

    private void setImageFromUri(Uri uri) {
        if (uri != null) {
            Bitmap bmp = BitmapFactory.decodeFile(uri.getPath());
            if (bmp != null) {
                ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
                BitmapDrawable icon = (BitmapDrawable) DrawableHelper.getDrawableFromBitmap(bmp, ICON_WIDTH, ICON_HEIGHT);
                actionBar.setIcon(icon);
                return;
            }
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
    }

    private void onDetailFinish(int mode) {
        if (mDetailController != null) {
            final DetailController detailController = mDetailController.get();
            if (detailController != null) {
                detailController.OnDetailFinish(mode, mEngine);
            }
        }
    }

    private boolean isValid() {
        String url = StringHelper.getStringFromCharSequence(mFieldUrl.getText());
        String name = StringHelper.getStringFromCharSequence(mFieldName.getText());

        if (StringHelper.isNullOrEmpty(name)) {
            notifyValidity(mFieldName, R.string.error_name_required);
            return false;
        }

        if (StringHelper.isNullOrEmpty(url)) {
            notifyValidity(mFieldUrl, R.string.error_url_required);
            return false;
        }

        if (!UrlHelper.isUrlValid(url.replace(SearchEngine.SEARCH_TERM, ""))) {
            notifyValidity(mFieldUrl, R.string.error_url_invalid);
            return false;
        }

        if (!url.contains(SearchEngine.SEARCH_TERM)) {
            notifyValidity(mFieldUrl, R.string.error_url_missing_term);
            return false;
        }

        return true;
    }
    private void notifyValidity(View view, int stringResourceId) {
        view.requestFocus();
        int[] loc = new int[2]; view.getLocationOnScreen(loc);
        Toast toast = Toast.makeText(getActivity(), stringResourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.START | Gravity.TOP, loc[0] + 32, loc[1] - view.getHeight()/2);
        toast.setMargin(0, 0);
        toast.show();
    }

    public void Cancel() {
        onDetailFinish(MODE_CANCEL);
    }

    public void Save() {
        if (isValid()) {
            mEngine = getData();
            DataProvider.updateSearchEngine(getActivity(), mEngine);
            onDetailFinish(MODE_UPDATE);
        }
    }

    public void Delete() {
        if (mEngine.id == null) {
            onDetailFinish(MODE_DELETE);
            return;
        }

        DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    DataProvider.deleteSearchEngine(getActivity(), getData());
                    onDetailFinish(MODE_DELETE);
                }
            }
        };

        new AlertDialog.Builder(getActivity())
            .setTitle(R.string.dialog_confirmation)
            .setMessage(R.string.confirmation_delete)
            .setCancelable(true)
            .setNegativeButton(R.string.no, click)
            .setPositiveButton(R.string.yes, click)
            .show();
    }

    public static final int MODE_DELETE     = -1;
    public static final int MODE_CANCEL     = 0;
    public static final int MODE_UPDATE     = 1;

    public static interface DetailController
    {
        void OnDetailFinish(int mode, SearchEngine engine);
    }
}


