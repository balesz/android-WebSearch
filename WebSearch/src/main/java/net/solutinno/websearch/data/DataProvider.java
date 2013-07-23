package net.solutinno.websearch.data;

import android.content.Context;

import org.apache.http.ConnectionReuseStrategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataProvider
{
    public static List<SearchEngine> getSearchEngines(Context context) {
        Database db = new Database(context);
        List<SearchEngine> result = null;
        try { result = db.engine.queryBuilder().orderBy("name", true).query(); }
        catch (SQLException e) { }
        db.close();
        return result;
    }

    public static SearchEngine getSearchEngine(Context context, UUID id) {
        Database db = new Database(context);
        SearchEngine result = db.engine.queryForId(id);
        db.close();
        return result;
    }

    public static void updateSearchEngine(Context context, SearchEngine engine) {
        Database db = new Database(context);
        if (engine.id == null) engine.id = UUID.randomUUID();
        db.engine.createOrUpdate(engine);
        db.close();
    }

    public static void deleteSearchEngine(Context context, SearchEngine engine) {
        Database db = new Database(context);
        db.engine.deleteById(engine.id);
        db.close();
    }

}
