package net.solutinno.util;

import java.net.URL;

public class UrlHelper
{
    public static boolean IsUrlValid(String url) {
        try { new URL(url); return true; } catch (Exception ex) { return false; }
    }
}
