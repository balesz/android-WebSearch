package net.solutinno.websearch.data;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfig extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
        SearchEngine.class
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
