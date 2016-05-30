package com.openread.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncodeTool {
    public String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
