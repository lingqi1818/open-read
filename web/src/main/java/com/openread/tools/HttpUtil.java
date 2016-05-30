package com.openread.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

    private static HttpClientHelper    hHelper  = new HttpClientHelper(false);
    public static List<HttpHost>       ipList   = new CopyOnWriteArrayList<HttpHost>();
    public static Set<HttpHost>        okList   = new HashSet<HttpHost>();
    public static Map<String, Integer> ipUseMap = new ConcurrentHashMap<String, Integer>();
    public static Random               r        = new Random();
    private static FileOutputStream    fos;
    private static int                 count    = 0;

    static {
        //        okList.add(new HttpHost("14.120.159.250", 8080));
        //        okList.add(new HttpHost("113.122.2.181", 9999));
        //okList.add(new HttpHost("124.160.127.102", 80));
        //        okList.add(new HttpHost("106.2.86.87", 80));
        //        okList.add(new HttpHost("116.231.194.98", 9999));
        //        okList.add(new HttpHost("59.39.232.218", 9999));
        //        okList.add(new HttpHost("114.237.131.80", 9999));
        //        okList.add(new HttpHost("110.182.125.15", 8118));
        //        okList.add(new HttpHost("110.53.64.13", 8080));
        //okList.add(new HttpHost("116.231.194.98", 9999));
        //okList.add(new HttpHost("182.253.73.170", 8080));
    }

    public static Header[] makeGenHeaders(String header) {
        return new Header[] { new BasicHeader("Host", header),
                new BasicHeader("Connection", "keep-alive"),
                new BasicHeader("Cache-Control", " max-age=0"),
                new BasicHeader(" Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
                new BasicHeader("Upgrade-Insecure-Requests", "1"),
                new BasicHeader("User-Agent",
                        " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36"),
                new BasicHeader("Accept-Encoding", " gzip, deflate, sdch"),
                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6") };
    }

    public static Map<String, Object> sendGetWithProxy(String url, String charset) {
        System.out.println("ipList size:" + ipList.size());
        System.out.println("okList size:" + okList.size());
        count++;
        Map<String, Object> result = new HashMap<String, Object>();
        if (ipList.size() == 0) {
            initIpList();
        }

        int index = r.nextInt(ipList.size());
        HttpHost host = ipList.get(index);
        if (okList.size() > 1) {
            int okindex = r.nextInt(okList.size());
            int i = 0;
            if (count % 3 == 0) {
                for (HttpHost h : okList) {
                    if (i == okindex) {
                        host = h;
                        break;
                    }
                    i++;
                }
            }
        }
        try {

            result.put("ip", host);
            result.put("result", sendGetWithProxy(url, charset, host));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(host);
            ipList.remove(host);
            return sendGetWithProxy(url, charset);
        }
    }

    private static void initIpList() {
        String result = HttpUtil.sendGet(
                "http://api.goubanjia.com/api/get.shtml?order=571de1c97a7d6f346a399d1040cc0bd3&num=100&carrier=0&protocol=0&an1=1&an2=2&an3=3&sp1=1&sp2=2&sp3=3&sort=1&system=1&distinct=0&rettype=1&seprator=%0D%0A",
                "gbk", false, "api.goubanjia.com");
        String ips[] = result.split("\n");
        for (String ip : ips) {
            String ip_kv[] = ip.split(":");
            try {
                String k1 = ip_kv[0];
                String k2 = ip_kv[1].trim().replace("\r", "");
                ipList.add(new HttpHost(k1, Integer.valueOf(k2)));
            } catch (Exception ex) {
                continue;
            }
        }
    }

    public static String sendGetWithProxy(String url, String charset, HttpHost host)
            throws Exception {
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(2000)
                .setConnectTimeout(2000).setConnectionRequestTimeout(2000)
                .setStaleConnectionCheckEnabled(true).build();
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig).setProxy(host).build();
        HttpGet request = new HttpGet(url);
        request.setHeaders(makeGenHeaders("www.dianping.com"));
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            if (fos == null) {
                fos = new FileOutputStream("/home/sysop/ip.txt", true);
            }
            String ip = "ok_host:" + host.getHostName() + ":" + host.getPort();
            Integer useNum = ipUseMap.get(host.toHostString());
            if (useNum == null) {
                ipUseMap.put(host.toHostString(), 1);
            } else {
                useNum++;
                ipUseMap.put(host.toHostString(), useNum);
            }
            if (useNum != null && useNum > 100) {
                okList.add(host);
            }
            fos.write(ip.getBytes());
            fos.write("\n".getBytes());
            String result = EntityUtils.toString(response.getEntity(), charset).trim();
            //System.out.println(result);
            return result;
        } catch (Exception ex) {
            throw ex;
        } finally {

            try {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String sendGet(String url, String charset, boolean isSSL, String host) {
        CloseableHttpResponse response = null;
        try {
            HttpGet request = new HttpGet(url);
            CloseableHttpClient hclient = null;
            request.setHeaders(makeGenHeaders(host));
            if (isSSL) {
                hclient = hHelper.createSSLClientDefault();
            } else {
                hclient = hHelper.getClient();
            }
            response = hclient.execute(request);
            return EntityUtils.toString(response.getEntity(), charset).trim();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws Exception {
        System.out.println(sendGet("http://www.sina.com", "gb2312", true, "www.sina.com"));
    }
}
