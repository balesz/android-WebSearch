package net.solutinno.websearch.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import net.solutinno.websearch.Application;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DataProvider
{
    public static List<SearchEngine> getSearchEngines(Context context) {
        Database db = new Database(context);
        List<SearchEngine> result = null;
        try {
            result = db.engine.queryBuilder().orderBy("name", true).query();
            for (SearchEngine item : result) {
                item.imageUri = Uri.fromFile(new File(Application.cacheDir, item.id + ".png"));
            }
        }
        catch (SQLException e) { }
        db.close();
        return result;
    }

    public static SearchEngine getSearchEngine(Context context, UUID id) {
        Database db = new Database(context);
        SearchEngine result = db.engine.queryForId(id);
        if (result != null) {
            result.imageUri = Uri.fromFile(new File(Application.cacheDir, result.id + ".png"));
        }
        db.close();
        return result;
    }

    public static void updateSearchEngine(Context context, SearchEngine engine) {
        Database db = new Database(context);
        if (engine.id == null) engine.id = UUID.randomUUID();
        db.engine.createOrUpdate(engine);
        if (engine.image != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(Application.cacheDir, engine.id + ".png"));
                Bitmap bitmap = ((BitmapDrawable)engine.image).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    public static void deleteSearchEngine(Context context, SearchEngine engine) {
        Database db = new Database(context);
        db.engine.deleteById(engine.id);
        db.close();
        new File(Application.cacheDir, engine.id + ".png").delete();
    }

}
