package com.openread.tools;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleSearchHelper {
    public static String      DBANK         = "dbank";
    public static String      VDISK         = "vdisk";
    public static String      BAIDU_PAN     = "百度网盘";
    public static String      ERROR_URL     = "error";
    private BaiduSearchHelper bsearchHelper = new BaiduSearchHelper();

    public GoogleSearchHelper() {
    }

    public List<String> getUrlFromGoogle(String title) {
        String res;
        List<String> resList = new ArrayList<String>();
        try {
            res = getUrlFromGoogle(title, DBANK);
            if (res != null) {
                resList.add(res);
            }
            res = getUrlFromGoogle(title, VDISK);
            if (res != null) {
                resList.add(res);

            }
            res = BaiduSearchHelper.parseUrl(getUrlFromGoogle(title, BAIDU_PAN), true);
            if (res != null) {
                resList.add(res);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (resList.size() <= 0) {
            resList.addAll(bsearchHelper.getUrlFromBaidu(title));
        }
        if (resList.size() <= 0) {
            resList.add(ERROR_URL);
        }
        return resList;
    }

    public String getUrlFromGoogle(String title, String site) throws Exception {
        String sb = HttpUtil.sendGet(
                "http://www.google.com/search?q="
                        + URLEncoder.encode(title + " pdf " + site, "UTF-8"), "gb2312", false,"www.gooogle.com");
        Document doc = Jsoup.parse(sb);
        Elements elements = doc.getElementById("ires").select("ol").select("li");
        String res = null;
        boolean isFind = false;
        if (elements.size() > 0) {
            for (Element e : elements) {
                res = e.select("a").attr("href");
                if (validateUrl(res, site)) {
                    isFind = true;
                    break;
                } else {
                    continue;
                }
            }
        }

        if (!isFind) {
            return ERROR_URL;
        }
        return res.split("\\?")[1].split("&")[0].split("=")[1];

    }

    public static boolean validateUrl(String url, String site) {
        if ((url.contains("dl.dbank.com") || url.contains("vmall.com")) && DBANK.equals(site)) {
            return true;
        }
        if (VDISK.equals(site) && url.contains("vdisk.weibo.com")) {
            return true;
        }
        if (url.contains("pan.baidu.com") && BAIDU_PAN.equals(site)) {
            return true;
        }
        return false;
    }

    public static void main(String args[]) throws Exception {
        System.out.println(new GoogleSearchHelper().getUrlFromGoogle("代码之美"));
    }

}
