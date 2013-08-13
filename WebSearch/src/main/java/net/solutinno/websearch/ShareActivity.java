package net.solutinno.websearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import net.solutinno.util.UrlHelper;

public class ShareActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String searchTerm = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        if (UrlHelper.IsUrlValid(searchTerm)) {
            ImportFragment fragment = ImportFragment.newInstance(searchTerm);
            fragment.setOnImportDialogResult(new ImportFragment.ImportDialogResult() {
                @Override
                public void OnDialogResult(int result) {
                    finish();
                }
            });
            fragment.show(getSupportFragmentManager(), ImportFragment.class.getName());
        }
        else {
            SearchFragment fragment = SearchFragment.newInstance(searchTerm);
            fragment.show(getSupportFragmentManager(), SearchFragment.class.getName());
        }
    }
}
