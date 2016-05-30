package com.openread.action;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codeanywhere.easyRestful.base.BaseAction;
import org.codeanywhere.easyRestful.base.annotation.SpringBean;

import com.openread.service.DailyDownloadService;
import com.openread.service.DailyRecommendService;
import com.openread.tools.DoubanBookParser.Book;

public class Guess extends BaseAction {
    @SpringBean
    private DailyRecommendService dailyRecommendService;
    private static String[]       keywords = { "畅销", "计算机", "历史", "政治", "经济", "小说", "军事", "哲学" };

    public void execute() {
        String job = "";
        String favs[] = this.getRequestContext().getHttpServletRequest().getParameterValues("fav");
        if (favs != null && favs.length > 0) {
            for (String fav : favs) {
                if ("畅销".equals(fav)) {
                    job = "路边社社长";
                    break;
                }
                if ("计算机".equals(fav)) {
                    job = "挨踢民工";
                    break;
                }
                if ("历史".equals(fav)) {
                    job = "下岗工人";
                    break;
                }
                if ("政治".equals(fav)) {
                    job = "阴谋家";
                    break;
                }
                if ("经济".equals(fav)) {
                    job = "股市高手";
                    break;
                }
                if ("小说".equals(fav)) {
                    job = "泡面宅人";
                    break;
                }
                if ("军事".equals(fav)) {
                    job = "战争狂人";
                    break;
                }
                if ("哲学".equals(fav)) {
                    job = "精神病院常驻人士";
                    break;
                }
            }
        }
        Map<String, Book> map = new HashMap<String, Book>();
        for (int i = 0; i < 2; i++) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -i);
                String day = DailyDownloadService.sdf.format(cal.getTime());
                List<Book> books = dailyRecommendService.getCurrentDayBooks(day);
                if (books != null) {
                    for (Book book : books) {
                        map.put(book.getTitle(), book);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.getRequestContext().put("bookMap", map);
        this.getRequestContext().put("job", job);
    }
}
