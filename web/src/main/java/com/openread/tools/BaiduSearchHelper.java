package com.openread.tools;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BaiduSearchHelper {

    public List<String> getUrlFromBaidu(String title) {
        List<String> resList = new ArrayList<String>();
        try {
            String res = getUrlFromBaidu(title, GoogleSearchHelper.DBANK);
            if (res != null && !"error".equals(res)) {
                resList.add(res);
            }
            res = getUrlFromBaidu(title, GoogleSearchHelper.VDISK);
            if (res != null && !"error".equals(res)) {
                resList.add(res);

            }
            res = parseUrl(getUrlFromBaidu(title, GoogleSearchHelper.BAIDU_PAN), true);
            if (res != null && !"error".equals(res)) {
                resList.add(res);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resList;
    }

    public String getUrlFromBaidu(String title, String site) throws Exception {
        Document doc = Jsoup.parse(HttpUtil.sendGet(
                "http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd="
                        + URLEncoder.encode(title + " pdf " + site, "UTF-8"),
                "UTF-8", true, "www.baidu.com"));
        Elements elements = doc.getElementById("content_left").select(".c-container");
        boolean isFind = false;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                String bUrl = e.select("a").get(0).attr("abs:href");
                String url = parseUrl(bUrl, false);
                if (validateUrl(url, site)) {
                    isFind = true;
                    return url;
                }
            }
        }
        if (!isFind) {
            return GoogleSearchHelper.ERROR_URL;
        }
        return doc.getElementById("content_left").select(".c-container").get(0).select("a").get(0)
                .attr("abs:href");
    }

    private boolean validateUrl(String url, String site) throws Exception {
        return GoogleSearchHelper.validateUrl(url, site);
    }

    public static String parseUrl(String url, boolean followRedirect) {
        System.out.println("parseurl:" + url);
        try {
            if (GoogleSearchHelper.ERROR_URL.equals(url)) {
                return url;
            }
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            for (Header head : HttpUtil.makeGenHeaders("www.baidu.com")) {
                conn.setRequestProperty(head.getName(), head.getValue());
            }
            conn.setInstanceFollowRedirects(followRedirect);
            conn.getInputStream();
            String res = conn.getHeaderField("Location");
            System.out.println("parse result:" + res);
            return res;
        } catch (Exception ex) {
            ex.printStackTrace();
            return GoogleSearchHelper.ERROR_URL;
        }
    }

    public static void main(String args[]) throws Exception {
        System.out.println(new BaiduSearchHelper().getUrlFromBaidu("从0到1"));

    }
}
