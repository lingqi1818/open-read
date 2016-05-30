package com.openread.action.inter;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codeanywhere.easyRestful.base.BaseAction;
import org.codeanywhere.easyRestful.base.RequestContext;
import org.codeanywhere.easyRestful.base.annotation.Request;
import org.codeanywhere.easyRestful.base.annotation.SpringBean;

import com.openread.pojo.Articles;
import com.openread.pojo.InMessage;
import com.openread.pojo.OutMessage;
import com.openread.service.DailyRecommendService;
import com.openread.tools.MessageProcessingHandler;
import com.openread.tools.SHA1;
import com.openread.tools.Tools;
import com.openread.tools.XStreamFactory;
import com.thoughtworks.xstream.XStream;

public class Qq extends BaseAction {
    private static final String   TOKEN = "openread_test_123456";
    @Request
    private RequestContext        context;
    @SpringBean
    private DailyRecommendService dailyRecommendService;

    public void execute() {
        try {
            HttpServletResponse response = context.getHttpServletResponse();
            HttpServletRequest request = context.getHttpServletRequest();
            if (request.getParameter("echostr") == null) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/xml");

                OutMessage oms = new OutMessage();
                ServletInputStream in = request.getInputStream();
                //转换微信post过来的xml内容
                XStream xs = XStreamFactory.init(false);
                xs.alias("xml", InMessage.class);
                String xmlMsg = Tools.inputStream2String(in);
                InMessage msg = (InMessage) xs.fromXML(xmlMsg);

                try {
                    //加载处理器
                    Class<?> clazz = Class
                            .forName("com.openread.tools.MessageProcessingHandlerImpl");
                    MessageProcessingHandler processingHandler = (MessageProcessingHandler) clazz
                            .newInstance();
                    processingHandler.setDailyRecommendService(dailyRecommendService);
                    //取得消息类型
                    String type = msg.getMsgType();
                    //针对不同类型消息进行处理
                    if (type.equals(MessageProcessingHandler.MSG_TYPE_TEXT)) {
                        oms = processingHandler.textTypeMsg(msg);
                    } else if (type.equals(MessageProcessingHandler.MSG_TYPE_LOCATION)) {
                        oms = processingHandler.locationTypeMsg(msg);
                    } else if (type.equals(MessageProcessingHandler.MSG_TYPE_LINK)) {
                        oms = processingHandler.linkTypeMsg(msg);
                    } else if (type.equals(MessageProcessingHandler.MSG_TYPE_IMAGE)) {
                        oms = processingHandler.imageTypeMsg(msg);
                    } else if (type.equals(MessageProcessingHandler.MSG_TYPE_EVENT)) {
                        oms = processingHandler.eventTypeMsg(msg);
                        //oms = processingHandler.textTypeMsg(msg);
                    }
                    if (oms == null) {
                        oms = new OutMessage();
                        oms.setContent("系统错误!");
                    }
                    //设置发送信息
                    oms.setCreateTime(new Date().getTime());
                    oms.setToUserName(msg.getFromUserName());
                    oms.setFromUserName(msg.getToUserName());
                } catch (ClassNotFoundException e) {
                    oms.setContent("系统错误！");
                } catch (InstantiationException e) {
                    oms.setContent("系统错误！");
                } catch (IllegalAccessException e) {
                    oms.setContent("系统错误！");
                }

                //把发送发送对象转换为xml输出
                xs = XStreamFactory.init(false);
                xs.alias("xml", OutMessage.class);
                xs.alias("item", Articles.class);
                xs.toXML(oms, response.getWriter());
                //xs.toXML(oms, System.out);
            }
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            String reSignature = null;
            try {
                String[] str = { TOKEN, timestamp, nonce };
                Arrays.sort(str);
                String bigStr = str[0] + str[1] + str[2];
                reSignature = new SHA1().getDigestOfString(bigStr.getBytes()).toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != reSignature && reSignature.equals(signature)) {
                out.print(echostr);
            } else {
                out.print("error request! the request is not from weixin server");
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
