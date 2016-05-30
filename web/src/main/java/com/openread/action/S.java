package com.openread.action;

import org.codeanywhere.easyRestful.base.BaseAction;

import com.openread.tools.GoogleSearchHelper;
import com.openread.tools.HttpClientHelper;

public class S extends BaseAction {
    private HttpClientHelper   hclientHelper = new HttpClientHelper(false);
    private GoogleSearchHelper gsearchHelper = new GoogleSearchHelper();

    public void execute() {
        //        try {
        //            getRequestContext().getHttpServletResponse().setCharacterEncoding("UTF-8");
        //            String key = getRequestContext().getHttpServletRequest().getParameter("key");
        //            String url = gsearchHelper.getUrlFromGoogle(key,
        //                    (CloseableHttpClient) hclientHelper.getClient());
        //            getRequestContext().put("key", key);
        //            getRequestContext().put("url", url);
        //
        //        } catch (Exception e) {
        //            throw new RuntimeException(e);
        //        }
    }

}
