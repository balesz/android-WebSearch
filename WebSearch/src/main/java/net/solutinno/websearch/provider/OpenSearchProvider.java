package net.solutinno.websearch.provider;

import net.solutinno.util.NetworkHelper;
import net.solutinno.util.UrlHelper;
import net.solutinno.websearch.data.SearchEngine;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.net.URL;

public class OpenSearchProvider
{
    public static SearchEngine GetEngine(String url) {
        SearchEngine result = null;
        try {
            if (!UrlHelper.isUrlValid(url)) return null;

            String content = NetworkHelper.downloadIntoText(new URL(url));

            if (content == null) throw new Exception("Download is failed!");

            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode root = cleaner.clean(content);

            if (IsOpenSearchXml(root)) {
                result = ReadOpenSearchXml(root);
                return result;
            }

            String osUrl = SearchOpenSearchXml(root);

            if (osUrl != null) {

                if (!UrlHelper.isUrlValid(osUrl)) {
                    URL srcUrl = new URL(url);
                    osUrl = srcUrl.getProtocol() + "://" + srcUrl.getHost() + osUrl;
                }

                content = NetworkHelper.downloadIntoText(new URL(osUrl));
                root = cleaner.clean(content);

                if (IsOpenSearchXml(root)) {
                    result = ReadOpenSearchXml(root);
                    return result;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static boolean IsOpenSearchXml(TagNode root) {
        try {
            Object[] nodes = root.evaluateXPath("//OpenSearchDescription");
            return nodes != null && nodes.length > 0;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String SearchOpenSearchXml(TagNode root) {
        try {
            Object[] nodes = root.evaluateXPath("//link[@rel='search'][@type='application/opensearchdescription+xml']/@href");
            return String.valueOf(nodes == null || nodes.length < 1 ? null : nodes[0]);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static SearchEngine ReadOpenSearchXml(TagNode root) {
        SearchEngine result = new SearchEngine();
        try {
            Object[] nodes = root.evaluateXPath("//ShortName");
            result.name = String.valueOf(nodes == null || nodes.length < 1 ? null : ((TagNode)nodes[0]).getText());

            nodes = root.evaluateXPath("//Description");
            result.description = String.valueOf(nodes == null || nodes.length < 1 ? null : ((TagNode)nodes[0]).getText());

            nodes = root.evaluateXPath("//Image");
            result.imageUrl = String.valueOf(nodes == null || nodes.length < 1 ? null : ((TagNode)nodes[0]).getText());

            nodes = root.evaluateXPath("//Url[@type='text/html']/@template");
            result.url = String.valueOf(nodes == null || nodes.length < 1 ? null : nodes[0]);

            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
