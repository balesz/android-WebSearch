package net.solutinno.websearch.provider;

import net.solutinno.util.NetworkHelper;
import net.solutinno.util.StreamHelper;
import net.solutinno.websearch.data.SearchEngine;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenSearchProvider
{
    private URL mUrl;
    private InputStream mStream;

    private String mContent;

    private HtmlCleaner mCleaner;
    private TagNode mRoot;

    public OpenSearchProvider(InputStream stream) {
        mStream = stream;
    }

    public OpenSearchProvider(String url) {
        try { mUrl = new URL(url); }
        catch (MalformedURLException e) { e.printStackTrace(); mUrl = null; }
    }

    public SearchEngine GetEngine() {
        try {
            if (mStream == null && mUrl == null) return null;
            downloadContent();
            parseContent();
            if (isOpenSearchXml()) return parseOpenSearchXml();
            else if (hasOpenSearchXml()) return new OpenSearchProvider(getOpenSearchXmlUrl()).GetEngine();
            else return null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void downloadContent() throws Exception {
        if (mUrl != null) mContent = NetworkHelper.downloadIntoText(mUrl);
        else if (mStream != null) mContent = StreamHelper.inputStreamToString(mStream, "UTF-8");
        if (mContent == null) throw new Exception(String.format("Download was failed! (Url: %s)", mUrl.toString()));
    }

    private void parseContent() {
        mCleaner = new HtmlCleaner();
        mRoot = mCleaner.clean(mContent);
    }

    private boolean isOpenSearchXml() {
        try {
            Object[] nodes = mRoot.evaluateXPath("//OpenSearchDescription");
            return nodes != null && nodes.length > 0;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean hasOpenSearchXml() {
        try {
            Object[] nodes = mRoot.evaluateXPath("//link[@rel='search'][@type='application/opensearchdescription+xml']");
            return nodes != null && nodes.length > 0;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private String getOpenSearchXmlUrl() {
        try {
            Object[] nodes = mRoot.evaluateXPath("//link[@rel='search'][@type='application/opensearchdescription+xml']/@href");
            return String.valueOf(nodes == null || nodes.length < 1 ? null : nodes[0]);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private SearchEngine parseOpenSearchXml() {
        SearchEngine result = new SearchEngine();
        try {
            Object[] nodes = mRoot.evaluateXPath("//ShortName");
            result.name = String.valueOf(nodes == null || nodes.length < 1 ? null : ((TagNode)nodes[0]).getText());

            nodes = mRoot.evaluateXPath("//Description");
            result.description = String.valueOf(nodes == null || nodes.length < 1 ? null : ((TagNode)nodes[0]).getText());

            nodes = mRoot.evaluateXPath("//Image");
            result.imageUrl = String.valueOf(nodes == null || nodes.length < 1 ? null : ((TagNode)nodes[0]).getText());

            nodes = mRoot.evaluateXPath("//Url[@type='text/html']/@template");
            result.url = String.valueOf(nodes == null || nodes.length < 1 ? null : nodes[0]);

            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
