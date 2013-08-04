package net.solutinno.websearch.provider;

import net.solutinno.util.NetworkHelper;
import net.solutinno.util.StringHelper;
import net.solutinno.websearch.data.SearchEngine;

import org.w3c.dom.Node;

import java.net.URL;

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
                XPathFactory factory = XPathFactory.newInstance();
                XPath path = factory.newXPath();
                Node node = (Node) path.evaluate("OpenSearchDescription", xmlString, XPathConstants.NODE);
                if (node == null) throw new Exception("The OpenSearchDescription node is not found!");

                node = (Node) path.evaluate("ShortName", xmlString, XPathConstants.NODE);
                result.name = node.getNodeValue();

                node = (Node) path.evaluate("Description", xmlString, XPathConstants.NODE);
                result.description = node.getNodeValue();

                node = (Node) path.evaluate("Description", xmlString, XPathConstants.NODE);
                result.description = node.getNodeValue();

                node = (Node) path.evaluate("Image", xmlString, XPathConstants.NODE);
                result.imageUrl = node.getNodeValue();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
