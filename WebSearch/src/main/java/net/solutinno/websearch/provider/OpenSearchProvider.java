package net.solutinno.websearch.provider;

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
    public static SearchEngine ReadOpenSearchXmlFromUrl(String url) {

        SearchEngine result = null;
        String xmlString;

        try { xmlString = NetworkHelper.DownloadText(new URL(url)); }
        catch (Exception ex) { xmlString = null; }

        if (!StringHelper.IsNullOrEmpty(xmlString)) {
            result = new SearchEngine();
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.parse(new ByteArrayInputStream(xmlString != null ? xmlString.getBytes("UTF-8") : new byte[0]));

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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
