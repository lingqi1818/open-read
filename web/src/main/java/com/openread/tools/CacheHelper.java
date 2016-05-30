package com.openread.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.openread.tools.DoubanBookParser.Book;
import com.openread.tools.InfoSearchHelper.Info;

public class CacheHelper {
    private int                     cacheSize   = 14;
    private int                     bookMapSize = 0;
    private int                     infoMapSize = 0;
    private Map<String, List<Book>> bookMap     = new ConcurrentHashMap<String, List<Book>>();
    private Map<String, List<Info>> infoMap     = new ConcurrentHashMap<String, List<Info>>();
    private static Object           infoLock    = new Object();
    private static CacheHelper      cacheHelper = new CacheHelper();
    private static String           infoPath    = OpenreadConfig
                                                        .getProp("openread.download.info.dir");
    private static String           charset     = "utf-8";

    public static CacheHelper getCacheHelper() {
        return cacheHelper;
    }

    public void putBooks(String key, List<Book> books) {
        bookMap.put(key, books);
        bookMapSize++;
        synchronized (CacheHelper.class) {
            if (bookMapSize > cacheSize) {
                List<String> keyList = new ArrayList<String>();
                keyList.addAll(bookMap.keySet());
                Collections.sort(keyList);
                bookMap.remove(keyList.get(0));
            }
        }

    }

    public void putInfos(String key, List<Info> infos) {
        infoMap.put(key, infos);
        writeInfosToFile(key, infos);
        infoMapSize++;
        synchronized (infoLock) {
            if (infoMapSize > cacheSize) {
                List<String> keyList = new ArrayList<String>();
                keyList.addAll(infoMap.keySet());
                Collections.sort(keyList);
                infoMap.remove(keyList.get(0));
                deleteInfos(keyList.get(0));
            }
        }

    }

    private void deleteInfos(String key) {
        File dir = new File(infoPath + "/" + key);
        dir.delete();
    }

    private void writeInfosToFile(String key, List<Info> infos) {
        String path = infoPath + "/" + key;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (Info info : infos) {
            try {
                writeInfo(info, path);
            } catch (Exception ex) {
                System.out.println("write info error:" + info.getTitle());
                continue;
            }
        }
    }

    private void writeInfo(Info info, String dir) throws Exception {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir
                + "/" + URLEncoder.encode(info.getTitle(), charset) + ".txt")));
        writer.write("author:" + URLEncoder.encode(info.getAuthor(), charset) + "\r\n");
        writer.write("context:" + URLEncoder.encode(info.getContext(), charset) + "\r\n");
        writer.write("createTime:" + URLEncoder.encode(info.getCreateTime(), charset) + "\r\n");
        writer.write("img:" + URLEncoder.encode(info.getImg(), charset) + "\r\n");
        writer.write("surl:" + URLEncoder.encode(info.getSurl(), charset) + "\r\n");
        writer.write("title:" + URLEncoder.encode(info.getTitle(), charset) + "\r\n");
        writer.close();
    }

    public List<Book> getBooks(String key) {
        return bookMap.get(key);
    }

    public List<Info> getInfos(String key) {
        List<Info> infoList = infoMap.get(key);
        if (infoList == null) {
            try {
                infoList = readInfosFromDir(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return infoList;
    }

    private List<Info> readInfosFromDir(String key) throws Exception {
        File currDir = new File(infoPath + "/" + key);
        List<Info> infoList = new ArrayList<InfoSearchHelper.Info>();
        if (!currDir.exists()) {
            return null;
        }
        String files[] = currDir.list();
        if (files == null || files.length <= 0) {
            return null;
        }
        for (String file : files) {
            File info = new File(currDir.getAbsolutePath() + "/" + file);
            Info infoObj = new Info();

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                    info)));
            String line;
            while ((line = reader.readLine()) != null) {

                String strs[] = line.split(":");
                if ("author".equalsIgnoreCase(strs[0])) {
                    infoObj.setAuthor(URLDecoder.decode(line.substring(strs[0].length() + 1),
                            charset));
                }
                if ("context".equalsIgnoreCase(strs[0])) {
                    infoObj.setContext(URLDecoder.decode(line.substring(strs[0].length() + 1),
                            charset));
                }
                if ("createTime".equalsIgnoreCase(strs[0])) {
                    infoObj.setCreateTime(URLDecoder.decode(line.substring(strs[0].length() + 1),
                            charset));
                }
                if ("img".equalsIgnoreCase(strs[0])) {
                    infoObj.setImg(URLDecoder.decode(line.substring(strs[0].length() + 1), charset));
                }
                if ("surl".equalsIgnoreCase(strs[0])) {
                    infoObj.setSurl(URLDecoder.decode(line.substring(strs[0].length() + 1), charset));
                }
                if ("title".equalsIgnoreCase(strs[0])) {
                    infoObj.setTitle(URLDecoder.decode(line.substring(strs[0].length() + 1),
                            charset));
                }

            }
            infoList.add(infoObj);
        }

        return infoList;
    }

    public static void main(String args[]) {
        String key = "20140101";
        //CacheHelper.getCacheHelper().putInfos(key, new NewsSpilder().craw(2));

        System.out.println(CacheHelper.getCacheHelper().getInfos(key).get(0).getTitle());
    }

}
