package net.solutinno.websearch;

import java.io.File;

public class Application extends android.app.Application
{
    public static File cacheDir = null;

    @Override
    public void onCreate() {
        super.onCreate();
        cacheDir = getApplicationContext().getCacheDir();
    }
}
