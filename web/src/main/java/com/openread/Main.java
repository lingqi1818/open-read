package com.openread;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.openread.service.DailyDownloadService;

public class Main {

    /**
     * java -Djava.ext.dirs=lib -cp classes com.openread.Main
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        DailyDownloadService downloadService = (DailyDownloadService) ac
                .getBean("dailyDownloadService");
        downloadService.download();
    }

}
