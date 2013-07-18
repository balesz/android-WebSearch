package net.solutinno.websearch.data;

import android.database.MatrixCursor;

public class SearchEngineCursor extends MatrixCursor
{
    public static final String[] COLUMNS = { SearchEngine.COLUMN_ID, SearchEngine.COLUMN_NAME, SearchEngine.COLUMN_DESCRIPTION, SearchEngine.COLUMN_URL, SearchEngine.COLUMN_IMAGE };

    public SearchEngineCursor() { super(COLUMNS); }

}
