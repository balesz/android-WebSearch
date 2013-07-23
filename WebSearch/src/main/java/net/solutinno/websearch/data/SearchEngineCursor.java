package net.solutinno.websearch.data;

import android.database.MatrixCursor;

import net.solutinno.websearch.R;

import java.util.ArrayList;
import java.util.List;

public class SearchEngineCursor extends MatrixCursor
{
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_IMAGE_URL = "IMAGE_URL";
    public static final String COLUMN_IMAGE = "IMAGE";

    public static final String[] COLUMNS = { "_id", COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_URL, COLUMN_IMAGE_URL, COLUMN_IMAGE };
    public static final String[] FIELDS = { COLUMN_IMAGE, COLUMN_NAME, COLUMN_DESCRIPTION };
    public static final int[] UI_FIELDS = { R.id.item_icon, R.id.item_text1, R.id.item_text2 };

    public SearchEngineCursor() { super(COLUMNS); }

    public static SearchEngineCursor createBySearchEngineList(List<SearchEngine> searchEngines) {
        SearchEngineCursor cursor = new SearchEngineCursor();

        int i = 0;
        for (SearchEngine item : searchEngines) {
            ArrayList<Object> values = new ArrayList<Object>();
            values.add(++i);
            values.add(item.id);
            values.add(item.name);
            values.add(item.description);
            values.add(item.url);
            values.add(item.imageUrl);
            values.add(item.image);
            cursor.addRow(values);
        }

        cursor.moveToFirst();

        return cursor;
    }

}
