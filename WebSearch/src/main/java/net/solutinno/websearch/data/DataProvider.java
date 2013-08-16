package net.solutinno.websearch.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import net.solutinno.util.NetworkHelper;
import net.solutinno.util.UrlHelper;
import net.solutinno.websearch.Application;
import net.solutinno.websearch.provider.OpenSearchProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DataProvider
{
    public static void fillDatabase(Context context) {
        Database db = new Database(context);
        if (db.engine.countOf() > 0) return;
        try {
            String[] engines = context.getAssets().list("");
            for (String engine : engines) {
                if (!engine.startsWith("os_")) continue;
                SearchEngine se = new OpenSearchProvider(context.getAssets().open(engine)).GetEngine();
                if (se != null) {
                    se.id = UUID.randomUUID();
                    db.engine.create(se);
                    downloadImageOfSearchEngine(se);
                }
            }
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SearchEngine> getSearchEngineList(Context context) {
        Database db = new Database(context);
        List<SearchEngine> result = null;
        try {
            result = db.engine.queryBuilder().orderBy("name", true).query();
            for (SearchEngine item : result) {
                item.imageUri = Uri.fromFile(getImageFileFromId(item.id));
            }
        }
        catch (SQLException e) { e.printStackTrace(); }
        db.close();
        return result;
    }

    public static SearchEngine getSearchEngine(Context context, UUID id) {
        Database db = new Database(context);
        SearchEngine result = db.engine.queryForId(id);
        if (result != null) {
            result.imageUri = Uri.fromFile(getImageFileFromId(result.id));
        }
        db.close();
        return result;
    }

    public static void updateSearchEngine(Context context, SearchEngine engine) {
        Database db = new Database(context);
        if (engine.id == null) engine.id = UUID.randomUUID();
        db.engine.createOrUpdate(engine);
        if (engine.imageUrl != null) {
            downloadImageOfSearchEngine(engine);
        }
        db.close();
    }

    public static boolean deleteSearchEngine(Context context, SearchEngine engine) {
        Database db = new Database(context);
        db.engine.deleteById(engine.id);
        db.close();
        return getImageFileFromId(engine.id).delete();
    }

    public static File getImageFileFromId(UUID id) {
        return new File(Application.cacheDir, id + ".png");
    }

    public static void downloadImageOfSearchEngine(SearchEngine engine) {
        File outFile = getImageFileFromId(engine.id);
        Bitmap bitmap = downloadImageToBitmap(engine.imageUrl);
        if (bitmap != null) {
            try { bitmap.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(outFile)); }
            catch (FileNotFoundException e) { e.printStackTrace(); }
        }
    }

    public static Bitmap downloadImageToBitmap(String url) {
        if (UrlHelper.isUrlValid(url)) {
            byte[] data = NetworkHelper.downloadIntoByteArray(UrlHelper.getUrlFromString(url));
            if (data != null) return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }

}
