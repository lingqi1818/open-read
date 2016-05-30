package com.openread.action;

import java.net.URLDecoder;
import java.util.List;

import org.codeanywhere.easyRestful.base.BaseAction;
import org.codeanywhere.easyRestful.base.annotation.SpringBean;

import com.openread.service.DailyRecommendService;
import com.openread.tools.CacheHelper;
import com.openread.tools.DoubanBookParser.Book;

public class Bookdetail extends BaseAction {
    @SpringBean
    private DailyRecommendService dailyRecommendService;

    public void get(String day, String title) {
        try {
            title = URLDecoder.decode(title, "UTF-8");
            if (CacheHelper.getCacheHelper().getBooks(day) == null) {
                CacheHelper.getCacheHelper()
                        .putBooks(day, dailyRecommendService.getCurrentDayBooks(day));
            }
            List<Book> books = CacheHelper.getCacheHelper().getBooks(day);
            if (books != null) {
                for (Book book : books) {
                    if (title.equals(book.getTitle())) {
                        getRequestContext().put("book", book);
                        break;
                    }
                }
            }
            if (!getRequestContext().getHttpServletRequest().getRequestURI().endsWith("/")) {
                getRequestContext().put("baseUrl", "../../../../");
            } else {
                getRequestContext().put("baseUrl", "../../../../../");
            }
            getRequestContext().put("day", day);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
