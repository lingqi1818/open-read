package com.openread.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codeanywhere.easyRestful.base.session.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.openread.tools.DoubanBookParser;
import com.openread.tools.DoubanBookParser.Book;

/**
 * 每日书籍推荐服务
 * 
 * @author chenke
 */
public class DailyRecommendService {
    private String           bookDir;
    private static String    charset     = "UTF-8";
    private DoubanBookParser dbookparser = new DoubanBookParser();

    public void setBookDir(String bookDir) {
        this.bookDir = bookDir;
    }

    public List<Book> getCurrentDayBooks(String day) throws Exception {
        File currDir = new File(bookDir + day);
        return parseBooks(currDir);
    }

    private List<Book> parseBooks(File currDir) throws Exception {
        List<Book> books = new ArrayList<Book>();
        if (!currDir.exists()) {
            return null;
        }
        String files[] = currDir.list();
        if (files == null || files.length <= 0) {
            return null;
        }
        for (String file : files) {
            File book = new File(currDir.getAbsolutePath() + "/" + file);
            Book bookObj = null;
            /*
             * writer.write("doubanUrl:" + doubanUrl + "\r\n");
             * writer.write("img:" + img + "\r\n"); writer.write("title:" +
             * URLEncoder.encode(title, charset) + "\r\n");
             * writer.write("comment:" + URLEncoder.encode(comment, charset) +
             * "\r\n"); writer.write("author:" + URLEncoder.encode(author,
             * charset) + "\r\n"); writer.write("downloadUrl:" +
             * URLEncoder.encode(downloadUrl, charset) + "\r\n");
             * writer.write("kw:" + URLEncoder.encode(kw, charset) + "\r\n");
             */
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(book)));
            String line;
            String author = null;
            String kw = null;
            String sintro = null;
            while ((line = reader.readLine()) != null) {

                String strs[] = line.split(":");
                if ("doubanUrl".equalsIgnoreCase(strs[0])) {
                    //String[] kv = parseURL();
                    bookObj = dbookparser.parse(
                            URLDecoder.decode(line.substring(strs[0].length() + 1), charset));
                    //                    bookObj.setDoubanUrl(URLDecoder.decode(line.substring(strs[0].length() + 1),
                    //                            charset));

                }
                //                if ("img".equalsIgnoreCase(strs[0])) {
                //                    bookObj.setImg(URLDecoder.decode(line.substring(strs[0].length() + 1), charset));
                //                }
                if ("comment".equalsIgnoreCase(strs[0])) {
                    sintro = URLDecoder.decode(line.substring(strs[0].length() + 1), charset);
                }
                if ("author".equalsIgnoreCase(strs[0])) {
                    author = URLDecoder.decode(line.substring(strs[0].length() + 1), charset);
                    //                                    bookObj.setAuthor(URLDecoder.decode(line.substring(strs[0].length() + 1),
                    //                                            charset));
                }
                if ("kw".equalsIgnoreCase(strs[0])) {
                    kw = URLDecoder.decode(line.substring(strs[0].length() + 1), charset);
                    // bookObj.setKw(URLDecoder.decode(line.substring(strs[0].length() + 1), charset));
                }
                if ("downloadUrl".equalsIgnoreCase(strs[0])) {
                    List<String> durls = new ArrayList<String>();
                    String[] surls = line.substring(strs[0].length() + 1).split("#");
                    if (surls != null) {
                        for (String surl : surls) {
                            durls.add(URLDecoder.decode(surl, charset));
                        }
                    }
                    bookObj.setDownloadUrls(durls);
                }
                //                if ("title".equalsIgnoreCase(strs[0])) {
                //                    bookObj.setTitle(URLDecoder.decode(line.substring(strs[0].length() + 1),
                //                            charset));
                //                }

            }
            if (bookObj != null) {
                bookObj.setKw(kw);
                bookObj.setAuthor(author);
                bookObj.setsIntro(sintro);
                bookObj.setUuid(new UUID().nextID());
                books.add(bookObj);
            }
            reader.close();
        }
        return books;
    }

    private static String[] parseURL(String url) {
        String strs[] = url.split("/");
        return new String[] { strs[2], url.substring(url.indexOf(strs[2]) + strs[2].length()) };
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        DailyRecommendService drs = (DailyRecommendService) ac.getBean("dailyRecommendService");
        String day = DailyDownloadService.sdf.format(new Date());
        List<Book> books = drs.getCurrentDayBooks(day);
        System.out.println(books.get(0).getTitle());
        System.out.println(books.get(0).getAuthor());
        System.out.println(books.get(0).getComments());
        String[] kv = parseURL("http://www.sina.com/test/abc/");
        System.out.println(kv[0]);
        System.out.println(kv[1]);
    }
}
