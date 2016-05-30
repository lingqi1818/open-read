package com.openread.action;

import java.util.Date;

import org.codeanywhere.easyRestful.base.BaseAction;
import org.codeanywhere.easyRestful.base.annotation.SpringBean;

import com.openread.service.DailyDownloadService;
import com.openread.service.DailyRecommendService;

public class Error extends BaseAction {
    @SpringBean
    private DailyRecommendService dailyRecommendService;

    public void execute() {
        String day = DailyDownloadService.sdf.format(new Date());
        getRequestContext().put("baseUrl", "./");
        getRequestContext().put("day", day);
    }

}
