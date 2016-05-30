package com.openread.action;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import org.codeanywhere.easyRestful.base.BaseAction;

import com.openread.service.DailyDownloadService;
import com.openread.tools.CacheHelper;
import com.openread.tools.NewsSpilder;

public class Info extends BaseAction {
    private NewsSpilder newsSpilder = new NewsSpilder();

    public void list(String day) {
        if (CacheHelper.getCacheHelper().getInfos(day) == null) {
            CacheHelper.getCacheHelper().putInfos(day, newsSpilder.craw(5));
        }
        getRequestContext().put("infos", CacheHelper.getCacheHelper().getInfos(day));
        if (!getRequestContext().getHttpServletRequest().getRequestURI().endsWith("/")) {
            getRequestContext().put("baseUrl", "../../../");
        } else {
            getRequestContext().put("baseUrl", "../../../../");
        }
        getRequestContext().put("day", day);
    }

    public void detail(String day, String title) {
        try {
            title = URLDecoder.decode(title, "UTF-8");
            if (CacheHelper.getCacheHelper().getInfos(day) == null) {
                CacheHelper.getCacheHelper().putInfos(day, newsSpilder.craw(5));

            }
            List<com.openread.tools.InfoSearchHelper.Info> infos = CacheHelper.getCacheHelper()
                    .getInfos(day);
            boolean isFind = false;
            if (infos != null) {
                for (com.openread.tools.InfoSearchHelper.Info info : infos) {
                    if (title.equals(info.getTitle())) {
                        isFind = true;
                        getRequestContext().put("info", info);
                        break;
                    }
                }
            }
            if (!isFind) {
                getRequestContext().getHttpServletResponse()
                        .sendRedirect("http://www.chinarendoc.com/info/list/attr/"
                                + DailyDownloadService.sdf.format(new Date()));
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
