package com.openread.action;

import org.codeanywhere.easyRestful.base.BaseAction;
import org.codeanywhere.easyRestful.base.annotation.SpringBean;

import com.openread.service.DailyRecommendService;
import com.openread.tools.CacheHelper;

public class Booklist extends BaseAction {
    @SpringBean
    private DailyRecommendService dailyRecommendService;

    public void detail(String day) {
        //String day = getRequestContext().getHttpServletRequest().getParameter("day");
        try {
            if (CacheHelper.getCacheHelper().getBooks(day) == null) {
                CacheHelper.getCacheHelper().putBooks(day,
                        dailyRecommendService.getCurrentDayBooks(day));
            }
            getRequestContext().put("books", CacheHelper.getCacheHelper().getBooks(day));
            getRequestContext().put("day", day);
            if (!getRequestContext().getHttpServletRequest().getRequestURI().endsWith("/")) {
                getRequestContext().put("baseUrl", "../../");
            } else {
                getRequestContext().put("baseUrl", "../../../");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
