package net.solutinno.websearch.data;

import android.net.Uri;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "search_engine")
public class SearchEngine
{
    public static final String SEARCH_TERM = "{searchTerms}";

    public SearchEngine() { }

    public SearchEngine(UUID id) {
        this.id = id;
    }

    @DatabaseField(id = true)
    public UUID id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField
    public String url;

    @DatabaseField
    public String imageUrl;

    public Uri imageUri;
}
