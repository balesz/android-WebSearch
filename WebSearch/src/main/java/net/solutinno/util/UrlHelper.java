package net.solutinno.util;

import java.net.URL;

public class UrlHelper
{
    public static boolean isUrlValid(String url) {
        try { new URL(url); return true; } catch (Exception ex) { return false; }
    }
}
