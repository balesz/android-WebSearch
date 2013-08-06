package net.solutinno.websearch.provider;

import android.util.Patterns;

import net.solutinno.util.NetworkHelper;
import net.solutinno.util.StringHelper;
import net.solutinno.websearch.data.SearchEngine;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class OpenSearchProvider
{
    private static Document getXmlDocument(String url) {
        Document result;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            String content = NetworkHelper.DownloadText(new URL(url));
            if (StringHelper.IsNullOrEmpty(content)) throw new Exception("Download error!");

            result = documentBuilder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
            if (result == null) throw new Exception("Xml parse error!");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Document getHtmlHead(String url) {
        Document result;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            String content = NetworkHelper.DownloadText(new URL(url));
            if (StringHelper.IsNullOrEmpty(content)) throw new Exception("Download error!");

            content = content.substring(content.indexOf("<head>"), content.indexOf("</head>")) + "</head>";

            result = documentBuilder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
            if (result == null) throw new Exception("Xml parse error!");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static SearchEngine GetEngine(String url) {
        SearchEngine result = null;
        try {
            Document doc = getXmlDocument(url);

            if (doc != null) {
                if (IsOpenSearchXml(doc)) {
                    result = ReadOpenSearchXml(doc);
                    return result;
                }
            }

            doc = getHtmlHead(url);
            String osUrl = SearchOpenSearchXml(doc);

            if (osUrl != null) {

                if (!Patterns.WEB_URL.matcher(osUrl).matches()) {
                    URL srcUrl = new URL(url);
                    osUrl = srcUrl.getProtocol() + "://" + srcUrl.getHost() + osUrl;
                }

                doc = getXmlDocument(osUrl);

                if (IsOpenSearchXml(doc)) {
                    result = ReadOpenSearchXml(doc);
                    return result;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static boolean IsOpenSearchXml(Document doc) {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath path = factory.newXPath();

            Node node = (Node) path.evaluate("OpenSearchDescription", doc, XPathConstants.NODE);
            if (node == null) throw new Exception("This document is not an OpenSearch markup xml!");

            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String SearchOpenSearchXml(Document doc) {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath path = factory.newXPath();

            Node node = (Node) path.evaluate("//link[@rel='search'][@type='application/opensearchdescription+xml']/@href", doc, XPathConstants.NODE);
            if (node == null) throw new Exception("The OpenSearchDescription typed link tag is not found!");

            return node.getTextContent();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static SearchEngine ReadOpenSearchXml(Document doc) {
        SearchEngine result = new SearchEngine();
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath path = factory.newXPath();

            Node root = (Node) path.evaluate("OpenSearchDescription", doc, XPathConstants.NODE);
            if (root == null) throw new Exception("The OpenSearchDescription node is not found!");

            Node node = (Node) path.evaluate("ShortName", root, XPathConstants.NODE);
            result.name = node == null ? null : node.getTextContent();

            node = (Node) path.evaluate("Description", root, XPathConstants.NODE);
            result.description = node == null ? null : node.getTextContent();

            node = (Node) path.evaluate("Image", root, XPathConstants.NODE);
            result.imageUrl = node == null ? null : node.getTextContent();

            node = (Node) path.evaluate("Url[@type='text/html']/@template", root, XPathConstants.NODE);
            result.url = node == null ? null : node.getTextContent();

            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
