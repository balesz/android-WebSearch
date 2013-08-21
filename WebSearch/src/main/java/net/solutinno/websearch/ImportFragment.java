package net.solutinno.websearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.solutinno.util.StringHelper;
import net.solutinno.util.UrlHelper;
import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.provider.OpenSearchProvider;

public class ImportFragment extends DialogFragment
{
    String mUrl;
    private RelativeLayout mContent;
    private ImportDialogResult mOnImportDialogResult;

    public static ImportFragment newInstance() {
        return new ImportFragment();
    }

    public static ImportFragment newInstance(String url) {
        ImportFragment result = new ImportFragment();
        result.mUrl = url;
        return result;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContent = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_import, null);
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.action_import)
            .setView(mContent)
            .setPositiveButton(R.string.caption_import, null)
            .setNegativeButton(R.string.caption_cancel, null)
            .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) this.getDialog();
        dialog.setCanceledOnTouchOutside(true);
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        assert button != null;
        button.setOnClickListener(mPositiveOnClick);
        button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        assert button != null;
        button.setOnClickListener(mNegativeOnClick);

        EditText fieldUrl = (EditText) mContent.findViewById(R.id.import_fieldImportFromUrl);
        fieldUrl.setText(mUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnImportDialogResult = null;
    }

    public void setOnImportDialogResult(ImportDialogResult onImportDialogResult) {
        mOnImportDialogResult = onImportDialogResult;
    }
    private void onImportDialogFinish(int result) {
        if (mOnImportDialogResult != null) {
            mOnImportDialogResult.OnDialogResult(result);
        }
    }

    private View.OnClickListener mPositiveOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final EditText mFieldImportUrl = (EditText) mContent.findViewById(R.id.import_fieldImportFromUrl);
            final ProgressBar mProgressBar = (ProgressBar) mContent.findViewById(R.id.import_progressBar);
            final String urlStr = StringHelper.getStringFromCharSequence(mFieldImportUrl.getText());
            if (!UrlHelper.isUrlValid(urlStr)) {
                Toast.makeText(getActivity(), R.string.error_import_url_invalid, Toast.LENGTH_LONG).show();
                return;
            }
            mProgressBar.setVisibility(View.VISIBLE);
            new AsyncTask<String, Integer, SearchEngine>() {
                @Override
                protected SearchEngine doInBackground(String... urls) {
                    SearchEngine result = new OpenSearchProvider(urls[0]).GetEngine();
                    if (result != null) DataProvider.updateSearchEngine(getActivity(), result);
                    return result;
                }
                @Override
                protected void onPostExecute(SearchEngine engine) {
                    Toast.makeText(getActivity(), engine == null ? R.string.information_import_unsuccessful : R.string.information_import_successful, Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.GONE);
                    if (engine != null) {
                        onImportDialogFinish(Activity.RESULT_OK);
                        dismiss();
                    }
                }
            }.execute(urlStr);
        }
    };

    private View.OnClickListener mNegativeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onImportDialogFinish(Activity.RESULT_CANCELED);
            dismiss();
        }
    };

    public static interface ImportDialogResult
    {
        void OnDialogResult(int result);
    }
}
