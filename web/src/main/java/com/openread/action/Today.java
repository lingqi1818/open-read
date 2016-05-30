package com.openread.action;

import java.util.Date;

import org.codeanywhere.easyRestful.base.BaseAction;
import org.codeanywhere.easyRestful.base.annotation.SpringBean;

import com.openread.service.DailyDownloadService;
import com.openread.service.DailyRecommendService;
import com.openread.tools.CacheHelper;

public class Today extends BaseAction {
    @SpringBean
    private DailyRecommendService dailyRecommendService;

    public void execute() {
        try {
            String day = DailyDownloadService.sdf.format(new Date());

            if (CacheHelper.getCacheHelper().getBooks(day) == null) {
                CacheHelper.getCacheHelper().putBooks(day,
                        dailyRecommendService.getCurrentDayBooks(day));
            }
            getRequestContext().put("books", CacheHelper.getCacheHelper().getBooks(day));
            getRequestContext().put("baseUrl", "./");
            getRequestContext().put("day", day);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
