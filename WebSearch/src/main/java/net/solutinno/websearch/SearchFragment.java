package net.solutinno.websearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import net.solutinno.websearch.data.DataProvider;
import net.solutinno.websearch.data.SearchEngine;
import net.solutinno.websearch.data.SearchEngineCursor;

import org.htmlparser.util.Translate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public class SearchFragment extends DialogFragment
{
    String mSearchTerm;

    public static SearchFragment newInstance(String searchTerm) {
        SearchFragment result = new SearchFragment();
        result.mSearchTerm = searchTerm;
        return result;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FrameLayout content = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_search, null);
        assert content != null;

        GridView searchGrid = (GridView) content.findViewById(R.id.search_grid);
        assert searchGrid != null;
        searchGrid.setOnItemClickListener(onItemClickListener);

        SearchEngineCursor cursor = SearchEngineCursor.createBySearchEngineList(DataProvider.getSearchEngineList(getActivity()));
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_search, cursor, SearchEngineCursor.GRID_FIELDS, SearchEngineCursor.GRID_UI_FIELDS, 0);
        searchGrid.setAdapter(adapter);

        return new AlertDialog.Builder(getActivity())
            .setTitle(mSearchTerm)
            .setView(content)
            .setCancelable(true)
            .setNegativeButton(R.string.caption_cancel, onNegativeButtonClick)
            .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        FrameLayout layout = (FrameLayout) getDialog().findViewById(R.id.search_content);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        assert params != null;
        params.height = (metrics.heightPixels/4);
        layout.setLayoutParams(params);
    }

    private DialogInterface.OnClickListener onNegativeButtonClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            getActivity().finish();
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SimpleCursorAdapter adapter = (SimpleCursorAdapter) adapterView.getAdapter();
            String id = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(SearchEngineCursor.COLUMN_ID));
            if (id == null) return;
            SearchEngine engine = DataProvider.getSearchEngine(getActivity(), UUID.fromString(id));
            try {
                String url = Translate.decode(engine.url).replace(SearchEngine.SEARCH_TERM, URLEncoder.encode(mSearchTerm, "UTF-8"));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            finally {
                dismiss();
                getActivity().finish();
            }
        }
    };
}
