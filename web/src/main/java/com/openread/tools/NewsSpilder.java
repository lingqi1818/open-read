package com.openread.tools;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.openread.tools.InfoSearchHelper.Info;
import com.openread.tools.InfoSearchHelper.InfoSpider;
import com.openread.tools.InfoSearchHelper.InfoType;

public class NewsSpilder implements InfoSpider {

    private String charset = "UTF-8";

    public List<Info> craw(int num) {
        List<Info> list = new ArrayList<InfoSearchHelper.Info>();
        list.addAll(crawFromDoNews(num));
        //        list.addAll(crawFromNeteast(num));
        //        list.addAll(crawFromQQ(num));
        //list.addAll(crawFromSina(num));
        //list.addAll(crawFromIfanr(num));
        //list.addAll(crawFromHuxiu(num));
        return list;
    }

    private List<Info> crawFromDoNews(int num) {
        List<Info> list = new ArrayList<InfoSearchHelper.Info>();
        Document doc = Jsoup.parse(HttpUtil.sendGet("http://www.donews.com/idonews/", "utf-8",
                false, "www.donews.com"));
        Elements elements = doc.select("li[class=twc]");
        int i = 0;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                i++;
                if (i > num) {
                    break;
                }
                Info info = new Info();
                info.setType(InfoType.NEWS);
                info.setImg(e.select("a").get(0).select("img").attr("src").replace("./",
                        "http://www.donews.com/idonews/"));
                String surl = "http://www.donews.com/idonews/" + e.select("a").get(0).attr("href");
                info.setSurl(surl);
                info.setTitle(e.select("a").get(0).attr("title"));
                String kv[] = e.select("a").get(3).text().split("•");
                info.setAuthor(kv[0]);
                info.setCreateTime(kv[1]);
                info.setContext(parseDoNewsDetail(surl));
                list.add(info);
            }
        }
        return list;
    }

    private List<Info> crawFromQQ(int num) {
        List<Info> list = new ArrayList<InfoSearchHelper.Info>();
        Document doc = Jsoup
                .parse(HttpUtil.sendGet("http://tech.qq.com/", "utf-8", false, "www.qq.com"));
        Elements elements = doc.getElementById("hotnews").select("div[class=Q-tpListInner]");
        int i = 1;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                if (i > num) {
                    break;
                }
                Info info = new Info();
                info.setType(InfoType.NEWS);
                info.setSurl(e.select("a[class=pic]").attr("href"));
                info.setImg(e.select("a[class=pic]").select("img").attr("src"));
                info.setTitle(e.select("a[class=pic]").select("img").attr("alt"));
                Document content = Jsoup
                        .parse(HttpUtil.sendGet(info.getSurl(), "utf-8", false, "www.qq"));
                try {
                    info.setContext(content.getElementById("C-Main-Article-QQ")
                            .select("div[class=bd]").html());
                } catch (Exception ex) {
                    continue;
                }
                i++;
                info.setAuthor("腾讯科技");
                info.setCreateTime(content.select("span[class=pubTime]").text());
                list.add(info);
            }
        }
        return list;
    }

    private List<Info> crawFromSina(int num) {
        List<Info> list = new ArrayList<InfoSearchHelper.Info>();
        Document doc = Jsoup.parse(
                HttpUtil.sendGet("http://tech.sina.com.cn/it/", "gb2312", false, "www.sina.com"));
        Elements elements = doc.select("ul[class=ul02]").select("li");
        int i = 0;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                if (!e.select("div[class=img]").select("a").attr("href").endsWith(".shtml")) {
                    continue;
                }
                i++;
                if (i > num) {
                    break;
                }

                Info info = new Info();
                info.setType(InfoType.NEWS);
                info.setTitle(e.select("a").get(0).text());
                info.setSurl(e.select("div[class=img]").select("a").attr("href"));
                info.setImg(e.select("div[class=img]").select("img").attr("src"));
                Document content = Jsoup
                        .parse(HttpUtil.sendGet(info.getSurl(), "gb2312", false, "www.sina.com"));
                info.setCreateTime(content.getElementById("pub_date").text());
                info.setContext(content.getElementById("artibody").html());
                info.setAuthor("新浪科技");
                list.add(info);
            }
        }
        return list;
    }

    private List<Info> crawFromIfanr(int num) {
        List<Info> list = new ArrayList<InfoSearchHelper.Info>();
        Document doc = Jsoup
                .parse(HttpUtil.sendGet("http://www.ifanr.com/", "gb2312", false, "www.ifanr.com"));
        Elements elements = doc.select("div[class=ifanr-recommend-items items1 S_fragment]")
                .select("a");
        int i = 0;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                i++;
                if (i > num) {
                    break;
                }
                Info info = new Info();
                info.setType(InfoType.NEWS);
                info.setSurl(e.attr("href"));
                info.setImg(e.select("img").attr("src"));
                info.setTitle(e.attr("title"));
                Document content = Jsoup
                        .parse(HttpUtil.sendGet(info.getSurl(), "gb2312", false, "www.fanr.com"));
                info.setContext(content.getElementById("entry-content").html());
                info.setAuthor("爱范儿");
                String kv[] = content.select("article").select("div[class=entry-meta]").text()
                        .split("\\|");
                info.setAuthor(kv[1]);
                info.setCreateTime(kv[0]);
                list.add(info);
            }
        }
        return list;
    }

    private List<Info> crawFromHuxiu(int num) {

        List<Info> list = new ArrayList<InfoSearchHelper.Info>();
        Document doc = Jsoup
                .parse(HttpUtil.sendGet("http://www.huxiu.com", "gbk", false, "www.huxiu.com"));
        Elements elements = doc.select("div[class=article-list idx-list]")
                .select("div[class*=clearfix article-box]");
        int i = 0;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                i++;
                if (i > num) {
                    break;
                }
                Info info = new Info();
                info.setSurl("http://www.huxiu.com" + e.select("a").attr("href"));
                info.setImg(e.select("img").attr("src"));
                info.setTitle(e.select("a").get(1).text());
                Document content = Jsoup
                        .parse(HttpUtil.sendGet(info.getSurl(), "gb2312", false, "www.huxiu.com"));
                info.setContext(content.select("tbody").select("td").select("div").html()
                        .replace("div", "p"));
                info.setAuthor(content.getElementById("author_baidu").text());
                info.setCreateTime(content.getElementById("pubtime_baidu").text());
                list.add(info);
            }
        }
        return list;
    }

    private List<Info> crawFromNeteast(int num) {
        List<Info> list = new ArrayList<InfoSearchHelper.Info>();
        Document doc = Jsoup
                .parse(HttpUtil.sendGet("http://tech.163.com", "gbk", false, "www.163.com"));
        Elements elements = doc.select("div[class=hot-list]").select("div[class=clearfix]");
        int i = 0;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                i++;
                if (i > num) {
                    break;
                }
                Info info = new Info();
                info.setSurl(e.select("a[class=left]").attr("href"));
                info.setImg(e.select("a[class=left]").select("img").attr("src"));
                String strs[] = e.select("h3[class=color-date-from]").html().split(" ");
                info.setAuthor(strs[0]);
                info.setCreateTime(strs[1] + " " + strs[2]);
                info.setTitle(e.select("h2[class=color-link]").select("a").text());
                info.setType(InfoType.NEWS);
                info.setContext(parseNeteastDetail(info.getSurl()));
                list.add(info);
            }
        }
        return list;
    }

    private String parseNeteastDetail(String surl) {
        Document doc = Jsoup.parse(HttpUtil.sendGet(surl, charset, false, "www.163.com"));
        return doc.getElementById("endText").html();
    }

    private String parseDoNewsDetail(String surl) {
        Document doc = Jsoup.parse(HttpUtil.sendGet(surl, charset, false, "www.donews.com"));
        return doc.select("div[class=post-page-wrapper-content]").html().replace("src=\"/idonews/",
                "src=\"http://www.donews.com/idonews/");
    }

    public static void main(String args[]) {
        new NewsSpilder().crawFromDoNews(2);
        //System.out.println(new NewsSpilder().crawFromNeteast(2).get(0).getContext());
        //System.out.println(new NewsSpilder().crawFromQQ(2).size());
        //System.out.println(new NewsSpilder().crawFromSina(2).get(0).getTitle());
        //System.out.println(new NewsSpilder().crawFromIfanr(2).get(0).getAuthor());
        //new NewsSpilder().crawFromHuxiu(2);
    }
}
