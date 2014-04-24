package net.solutinno.websearch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.solutinno.websearch.R;

import java.util.UUID;

public class Database extends OrmLiteSqliteOpenHelper
{
    private static final String DB_NAME = "search_engines.sqlite";
    private static final int DB_VERSION = 2;

    public final RuntimeExceptionDao<SearchEngine, UUID> engine;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION, R.raw.ormlite_config);
        engine = getRuntimeExceptionDao(SearchEngine.class);
    }

    public static boolean isExists(Context context) {
        return context.getDatabasePath(DB_NAME).exists();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.dropTable(connectionSource, SearchEngine.class, true);
            TableUtils.createTable(connectionSource, SearchEngine.class);
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) DataProvider.upgrade1to2(this);
    }
}
