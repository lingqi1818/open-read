package com.openread.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.openread.tools.HttpUtil;
import com.openread.tools.MongoHelper;

/**
 * 爬取点评商圈服务
 * 
 * @author chenke
 * @date 2016年5月10日 下午5:27:31
 */
public class CrawlBusinessAreaService {
    private static final Integer[] citys       = { 2, 3 };
    private static MongoHelper     mongoHelper = new MongoHelper();

    public void crawlCitys() {
        for (int cityId : citys) {
            crawlSingleCity(cityId);
        }
    }

    private void crawlSingleCity(int cityId) {
        String url = "http://www.dianping.com/shopall/" + cityId + "/0#BDBlock";
        Map<String, Object> resultMap = HttpUtil.sendGetWithProxy(url, "GBK");
        String result = (String) resultMap.get("result");
        Object ip = resultMap.get("ip");
        try {
            Document doc = Jsoup.parse(result);
            Elements areas = doc.select("div.shopallCate");
            for (Element e : areas) {
                String type = e.select("h2").text();
                if ("分类".equals(type)) {
                    continue;
                }
                if ("大学周边".equals(type)) {
                    //大学周边单独处理
                } else {
                    Elements lists = e.select("dl.list");
                    for (Element list : lists) {
                        String fName = list.select("a.Bravia").text();
                        Elements _list = list.select("a.B");
                        for (Element sub : _list) {
                            String subName = sub.text();
                            System.out.println("#" + type + ":" + fName + ":" + subName);
                            String areaUrl = sub.attr("href");
                            downloadCoordinateFromArea(cityId, type, fName, subName, areaUrl);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(result);
            HttpUtil.ipList.remove(ip);
            HttpUtil.okList.remove(ip);
            crawlSingleCity(cityId);
        }
    }

    private void downloadCoordinateFromArea(int cityId, String type, String fName, String subName,
                                            String areaUrl) {
        int n = 1;
        while (true) {
            if (!downloadCoordinateFromAreaForPage(cityId, type, fName, subName, areaUrl, n)) {
                break;
            }
            n++;
        }
    }

    private boolean downloadCoordinateFromAreaForPage(int cityId, String type, String fName,
                                                      String subName, String areaUrl, int page) {

        Map<String, Object> resultMap = HttpUtil
                .sendGetWithProxy("http://www.dianping.com" + areaUrl + "d1p" + page, "GBK");
        String result = (String) resultMap.get("result");
        Object ip = resultMap.get("ip");
        try {
            Document doc = Jsoup.parse(result);
            Elements eles = doc.getElementById("shop-all-list").select("li");
            int size = eles.size();
            System.out.println(
                    "fname:" + fName + ",subName:" + subName + ",page:" + page + ",size" + size);
            int i = 0;
            for (Element ele : eles) {
                i++;
                System.out.println(i);
                String purl = ele.select("a").get(0).attr("href");
                downloadSingleCoordinate(cityId, type, fName, subName, purl);
            }
            if (!doc.select("div.page").text().contains("下一页")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(result);
            HttpUtil.ipList.remove(ip);
            HttpUtil.okList.remove(ip);
            return downloadCoordinateFromAreaForPage(cityId, type, fName, subName, areaUrl, page);
        }
    }

    private void downloadSingleCoordinate(int cityId, String type, String fName, String subName,
                                          String purl) {
        Map<String, Object> resultMap = HttpUtil.sendGetWithProxy("http://www.dianping.com" + purl,
                "GBK");
        String result = (String) resultMap.get("result");
        Object ip = resultMap.get("ip");
        try {
            Document doc = Jsoup.parse(result);
            String address = "";
            if (doc.select("div.expand-info").size() > 0) {
                address = doc.select("div.expand-info").get(0).text();
                System.out.println(address);
            }
            Pattern pattern = Pattern
                    .compile("[\\s\\S]*\\(\\{lng:([0-9.]*),lat:([0-9.]*)\\}\\)[\\s\\S]*");
            Matcher matcher = pattern.matcher(result);
            if (matcher.matches()) {
                String lng = matcher.group(1);
                String lat = matcher.group(2);
                System.out.println("lng:" + lng);
                System.out.println("lat" + lat);
                Map<String, Object> obj = new HashMap<String, Object>();
                obj.put("cityId", cityId);
                obj.put("type", type);
                obj.put("first_name", fName);
                obj.put("second_name", subName);
                obj.put("address", address);
                obj.put("location", "[" + lng + "," + lat + "]");
                mongoHelper.insert("test", "business_area", obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(result);
            HttpUtil.ipList.remove(ip);
            HttpUtil.okList.remove(ip);
            downloadSingleCoordinate(cityId, type, fName, subName, purl);
        }
    }

    public static void main(String args[]) {
        new CrawlBusinessAreaService().crawlCitys();
    }
}
