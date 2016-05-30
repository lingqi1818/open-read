package com.openread.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.openread.pojo.Articles;
import com.openread.pojo.InMessage;
import com.openread.pojo.OutMessage;
import com.openread.service.DailyDownloadService;
import com.openread.service.DailyRecommendService;
import com.openread.tools.DoubanBookParser.Book;
import com.thoughtworks.xstream.XStream;

/**
 * 自定义实现消息处理
 * 
 * @author chenke
 */
public class MessageProcessingHandlerImpl implements MessageProcessingHandler {
    private DailyRecommendService dailyRecommendService;

    public OutMessage textTypeMsg(InMessage msg) {
        OutMessage oms = new OutMessage();
        if (MessageProcessingHandler.MSG_TYPE_TEXT.equalsIgnoreCase(msg.getMsgType())) {
            try {
                String day = DailyDownloadService.sdf.format(new Date());
                if (CacheHelper.getCacheHelper().getBooks(day) == null) {
                    CacheHelper.getCacheHelper().putBooks(day,
                            dailyRecommendService.getCurrentDayBooks(day));
                }
                List<Book> books = CacheHelper.getCacheHelper().getBooks(day);
                if (books != null && books.size() > 0) {
                    List<Articles> list = new ArrayList<Articles>();
                    oms.setMsgType(MSG_TYPE_NEWS);
                    oms.setArticleCount(books.size());
                    for (Book book : books) {
                        Articles a = new Articles();
                        a.setTitle(book.getTitle());
                        a.setDescription(book.getIntro());
                        a.setPicUrl(book.getLpic());
                        a.setUrl("http://www.dumpcache.com/bookdetail/get/attr/" + day + "/"
                                + book.getUuid());
                        list.add(a);
                    }
                    oms.setArticles(list);
                } else {
                    oms.setContent("今日暂无推荐");
                }
            } catch (Exception e) {
                oms.setContent("今日暂无推荐");
            }
        }
        return oms;
    }

    public OutMessage locationTypeMsg(InMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    public OutMessage imageTypeMsg(InMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    public OutMessage linkTypeMsg(InMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    public OutMessage eventTypeMsg(InMessage msg) {
        OutMessage oms = new OutMessage();
        oms.setContent("客官，欢迎您，我将会竭诚为您服务，请输入任意字符获取最新图书信息！");
        return oms;
    }

    public static void main(String args[]) {
        OutMessage oms = new OutMessage();
        oms.setMsgType(MSG_TYPE_NEWS);
        oms.setArticleCount(1);
        Articles a = new Articles();
        List<Articles> list = new ArrayList<Articles>();
        a.setTitle("news1");
        a.setDescription("hahaha");
        a.setPicUrl("http://img5.douban.com/mpic/s2659208.jpg");
        a.setUrl("http://www.sina.com");
        list.add(a);
        oms.setArticles(list);
        XStream xs = XStreamFactory.init(false);
        xs.alias("xml", OutMessage.class);
        xs.alias("item", Articles.class);
        xs.toXML(oms, System.out);
    }

    public void setDailyRecommendService(DailyRecommendService dailyRecommendService) {
        this.dailyRecommendService = dailyRecommendService;
    }

}
