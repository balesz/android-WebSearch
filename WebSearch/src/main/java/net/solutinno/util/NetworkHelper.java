package net.solutinno.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper
{
    public static byte[] downloadIntoByteArray(URL url) {
        if (url == null) return null;
        try {
            ByteArrayOutputStream outputStream = downloadIntoByteArrayOutputStream(url);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String downloadIntoText(URL url) {
        if (url == null) return null;
        try {
            String result;
            ByteArrayOutputStream outputStream = downloadIntoByteArrayOutputStream(url);
            result = outputStream.toString("UTF-8");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ByteArrayOutputStream downloadIntoByteArrayOutputStream(URL url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream input = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte data[] = new byte[1024]; int count;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
        }

        output.flush();
        output.close();
        input.close();

        return output;
    }
}
