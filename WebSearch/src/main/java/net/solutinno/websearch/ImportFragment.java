package net.solutinno.websearch;

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
    private RelativeLayout mContent;
    private ImportDialogResult mOnImportDialogResult;

    public static ImportFragment newInstance() {
        return new ImportFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContent = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.import_layout, null);
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
    }

    public void setOnImportDialogResult(ImportDialogResult onImportDialogResult) {
        mOnImportDialogResult = onImportDialogResult;
    }

    private View.OnClickListener mPositiveOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final EditText mFieldImportUrl = (EditText) mContent.findViewById(R.id.detail_fieldImportFromUrl);
            final ProgressBar mProgressBar = (ProgressBar) mContent.findViewById(R.id.detail_progressBar);
            final String urlStr = StringHelper.GetStringFromCharSequence(mFieldImportUrl.getText());
            if (!UrlHelper.IsUrlValid(urlStr)) {
                Toast.makeText(getActivity(), R.string.error_invalid_url, Toast.LENGTH_LONG).show();
                return;
            }
            mProgressBar.setVisibility(View.VISIBLE);
            new AsyncTask<String, Integer, SearchEngine>() {
                @Override
                protected SearchEngine doInBackground(String... urls) {
                    return OpenSearchProvider.GetEngine(urls[0]);
                }
                @Override
                protected void onPostExecute(SearchEngine engine) {
                    if (engine != null) {
                        DataProvider.updateSearchEngine(getActivity(), engine);
                        mOnImportDialogResult.OnDialogResult(1);
                        mProgressBar.setVisibility(View.GONE);
                        dismiss();
                    }
                }
            }.execute(urlStr);
        }
    };

    private View.OnClickListener mNegativeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mOnImportDialogResult.OnDialogResult(0);
            dismiss();
        }
    };

    public static interface ImportDialogResult
    {
        void OnDialogResult(int result);
    }
}
