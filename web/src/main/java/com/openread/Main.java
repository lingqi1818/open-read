package com.openread;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.openread.tools.BaiduSearchHelper;

public class Main {

    /**
     * java -Djava.ext.dirs=lib -cp classes com.openread.Main
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        //        DailyDownloadService downloadService = (DailyDownloadService) ac
        //                .getBean("dailyDownloadService");
        //        downloadService.download();
        System.out.println(new BaiduSearchHelper().getUrlFromBaidu("从0到1"));

        //String str = "<script>window.location.replace(\"http://www.panzz.com/t/VC9nFf\")</script><noscript><META http-equiv=\"refresh\" content=\"0;URL='http://www.panzz.com/t/VC9nFf'\"></noscript>";
        //System.out.println(str.split("URL='")[1].split("'")[0]);
    }

}
