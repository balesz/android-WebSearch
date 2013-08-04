package net.solutinno.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper
{
    public static byte[] DownloadBinary(URL url) {
        if (url == null) return null;
        try {
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

            return output.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DownloadText(URL url) {
        if (url == null) return null;
        try {
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

            String result;
            result = output.toString("UTF-8");
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
