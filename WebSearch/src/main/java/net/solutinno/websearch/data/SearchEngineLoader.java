package net.solutinno.websearch.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class SearchEngineLoader extends AsyncTaskLoader<List<SearchEngine>>
{
    List<SearchEngine> mSearchEngines;

    public SearchEngineLoader(Context context) {
        super(context);
    }

    @Override
    public List<SearchEngine> loadInBackground() {
        List<SearchEngine> result = DataProvider.getSearchEngines(getContext());
        return result;
    }

    @Override
    public void deliverResult(List<SearchEngine> data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        List<SearchEngine> old = mSearchEngines;
        mSearchEngines = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (old != data) {
            releaseResources(old);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mSearchEngines != null) {
            deliverResult(mSearchEngines);
        }
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mSearchEngines != null) {
            releaseResources(mSearchEngines);
            mSearchEngines = null;
        }
    }

    @Override
    public void onCanceled(List<SearchEngine> data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    void releaseResources(List<SearchEngine> data) {
        if (data == null) return;
        data.clear();
    }

}
