package com.openread.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.openread.tools.BaiduSearchHelper;
import com.openread.tools.HttpUtil;

/**
 * 每日下载服务
 * 
 * @author chenke
 */
public class DailyDownloadService {
    private static String[]        keywords      = { "互联网", "程序设计", "数据库", "操作系统", "大数据", "嵌入式",
            "管理", "营销" };
    private int                    count         = 0;
    private String                 countFile;
    private String                 downloadBaseDir;
    private String                 charset       = "UTF-8";
    public static SimpleDateFormat sdf           = new SimpleDateFormat("yyyyMMdd");
    //private GoogleSearchHelper     gsearchHelper = new GoogleSearchHelper();

    private BaiduSearchHelper      bsearchHelper = new BaiduSearchHelper();

    public void setDownloadBaseDir(String downloadBaseDir) {
        this.downloadBaseDir = downloadBaseDir;
    }

    public void setCountFile(String countFile) {
        this.countFile = countFile;
    }

    public DailyDownloadService() {
    }

    public void download() throws Exception {
        initCount();
        downloadBooks();
        writeCountToFile();
    }

    private void downloadBooks() throws Exception {
        String dataDir = downloadBaseDir + "/" + sdf.format(new Date());
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (String kw : keywords) {
            try {
                downloadSingleBook(kw, dataDir);
            } catch (Exception ex) {
                System.out.println("download error:" + kw);
                continue;
            }
        }
    }

    private void downloadSingleBook(String kw, String dir) throws Exception {
        int num = count % 20;
        Document doc = Jsoup
                .parse(HttpUtil
                        .sendGet(
                                "https://book.douban.com/tag/" + URLEncoder.encode(kw, "UTF-8")
                                        + "?start=" + count / 20 * 20 + "&type=T",
                                charset, true, "book.douban.com"));
        Elements list = doc.select(".subject-list");
        Elements items = list.select(".subject-item");
        if (num > items.size()) {
            num = items.size();
        }

        String doubanUrl = items.get(num).select("a").get(0).attr("abs:href");
        String img = items.get(num).select("img").get(0).attr("abs:src");
        img = img.replace("mpic", "lpic");
        String title = items.get(num).select("a").get(1).attr("title");
        Elements pElements = items.get(num).select("p");
        String comment = "";
        if (pElements.size() > 0) {
            comment = pElements.get(0).html();
        }
        String author = items.get(num).select(".pub").get(0).html().split("/")[0];
        List<String> downloadUrls = bsearchHelper.getUrlFromBaidu(title);

        StringBuilder durlSb = new StringBuilder();
        int i = 0;
        for (String durl : downloadUrls) {
            i++;
            durlSb.append(URLEncoder.encode(durl, charset));
            if (i != downloadUrls.size()) {
                durlSb.append("#");
            }

        }
        File book = new File(dir + "/" + URLEncoder.encode(title, charset) + ".txt");
        if (!book.exists()) {
            book.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(book)));
        writer.write("doubanUrl:" + doubanUrl + "\r\n");
        writer.write("img:" + img + "\r\n");
        writer.write("title:" + URLEncoder.encode(title, charset) + "\r\n");
        writer.write("comment:" + URLEncoder.encode(comment, charset) + "\r\n");
        writer.write("author:" + URLEncoder.encode(author, charset) + "\r\n");
        writer.write("downloadUrl:" + durlSb.toString() + "\r\n");
        writer.write("kw:" + URLEncoder.encode(kw, charset) + "\r\n");
        writer.close();
    }

    private void writeCountToFile() throws Exception {
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(countFile)));
        count = count + 2;
        writer.write(count + "\r\n");
        writer.close();
    }

    private void initCount() throws Exception {
        File cf = new File(countFile);
        if (!cf.exists()) {
            cf.createNewFile();
            writeCountToFile();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cf)));
        count = Integer.valueOf(reader.readLine());
        reader.close();
    }

    /**
     * @param args @throws Exception @throws
     */
    public static void main(String[] args) throws Exception {
        //HttpClient client = new HttpClient();
        //        HttpMethod method = new GetMethod(
        //                "http://www.baidu.com/s?ie=utf-8&mod=0&isid=bd18515b0009469f&pstg=0&wd=%E6%B5%AA%E6%BD%AE%E4%B9%8B%E5%B7%85%20pdf%20%E7%99%BE%E5%BA%A6%E7%BD%91%E7%9B%98");
        //HttpMethod method = new GetMethod(
        //        "http://www.baidu.com/link?url=KV-swrknHPaNbB52Bnu4mHy0QjHIJ9NkiZv88UNssIEziI6bHV5YNFR2hISaPRA5qEb99kPGuoenMV9rSfLm5D8u-Mxr3ItyxcOUcb1qDLYU3y9afmkZnfVBZiFCDqwW");
        //client.executeMethod(method);
        //System.out.println(method.getRequestHeader("Host"));
        //System.out.println(3 % 10);
        DailyDownloadService downloadService = new DailyDownloadService();
        downloadService.setCountFile("/Users/chenke/count.txt");
        downloadService.setDownloadBaseDir("/Users/chenke/books");
        downloadService.download();
        //        Document doc = Jsoup
        //                .connect(
        //                        "http://www.baidu.com/s?ie=utf-8&mod=0&isid=bd18515b0009469f&pstg=0&wd="
        //                                + URLEncoder.encode("浪潮之巅 pdf 百度网盘", "UTF-8")).timeout(0).get();
        //        System.out.println(doc.getElementById("content_left").select(".c-container").get(0)
        //                .select("a").get(0).attr("abs:href"));
        /*
         * Document doc = Jsoup .connect("http://book.douban.com/tag/" +
         * URLEncoder.encode("计算机", "UTF-8")) .timeout(0).get(); Elements list =
         * doc.select(".subject-list"); Elements items =
         * list.select(".subject-item"); System.out.println(items.size());
         * System.out.println(items.get(0).select("a").get(0).attr("abs:href"));
         * System
         * .out.println(items.get(0).select("img").get(0).attr("abs:src"));
         * System.out.println(items.get(0).select("a").get(1).attr("title"));
         */

        //        Document doc = Jsoup
        //                .connect(
        //                        "http://www.google.com/webhp#q="
        //                                + URLEncoder.encode("浪潮之巅 pdf dbank", "UTF-8")).timeout(5000).get();  
        //        HttpClientHelper helper = new HttpClientHelper(true);
        //        String url = new GoogleSearchHelper().getUrlFromGoogle("浪潮之巅",
        //                (CloseableHttpClient) helper.getClient());
        //        System.out.println(url);
        //        url = new GoogleSearchHelper().getUrlFromGoogle("代码之美",
        //                (CloseableHttpClient) helper.getClient());
        //        System.out.println(url);
    }
}
