package com.openread.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DoubanBookParser {

    public static class Book {
        private String       lpic;
        private String       mpic;
        private String       title;
        private List<String> properties    = new ArrayList<String>();
        private String       vote;
        private String       menu;
        private String       intro;
        private String       sIntro;
        private String       avgVoteStar;
        private String       kw;
        private String       author;
        private List<String> downloadUrls;

        private Set<String>  comments      = new HashSet<String>();
        private Set<String>  commentTitles = new HashSet<String>();
        private Set<String>  commentUsers  = new HashSet<String>();

        public String getsIntro() {
            return sIntro;
        }

        public void setsIntro(String sIntro) {
            this.sIntro = sIntro;
        }

        public List<String> getDownloadUrls() {
            return downloadUrls;
        }

        public void setDownloadUrls(List<String> downloadUrls) {
            this.downloadUrls = downloadUrls;
        }

        public String getKw() {
            return kw;
        }

        public void setKw(String kw) {
            this.kw = kw;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAvgVoteStar() {
            return avgVoteStar;
        }

        public void setAvgVoteStar(String avgVoteStar) {
            this.avgVoteStar = avgVoteStar;
        }

        public String getLpic() {
            return lpic;
        }

        public void setLpic(String lpic) {
            this.lpic = lpic;
        }

        public String getMpic() {
            return mpic;
        }

        public void setMpic(String mpic) {
            this.mpic = mpic;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getProperties() {
            return properties;
        }

        public void setProperties(List<String> properties) {
            this.properties = properties;
        }

        public String getVote() {
            return vote;
        }

        public void setVote(String vote) {
            this.vote = vote;
        }

        public String getMenu() {
            return menu;
        }

        public void setMenu(String menu) {
            this.menu = menu;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public Set<String> getComments() {
            return comments;
        }

        public void setComments(Set<String> comments) {
            this.comments = comments;
        }

        public List<String> getCommentTitles() {
            List<String> list = new ArrayList<String>();
            list.addAll(commentTitles);
            return list;
        }

        public void setCommentTitles(Set<String> commentTitles) {
            this.commentTitles = commentTitles;
        }

        public List<String> getCommentUsers() {
            List<String> list = new ArrayList<String>();
            list.addAll(commentUsers);
            return list;
        }

        public void setCommentUsers(Set<String> commentUsers) {
            this.commentUsers = commentUsers;
        }

    }

    public Book parse(String url) {
        Book book = new Book();
        Document doc = Jsoup.parse(HttpUtil.sendGet(url, "UTF-8", true, "book.douban.com"));
        Element mainPic = doc.getElementById("mainpic");
        book.setTitle(mainPic.select("a").attr("title"));
        book.setLpic(mainPic.getElementById("mainpic").select("a").attr("href"));
        book.setMpic(mainPic.select("img").attr("src"));
        Element info = doc.getElementById("info");
        String infoText = info.text().replace(" ", " ");
        String kvs[] = infoText.split(" ");
        for (int i = 0; i < kvs.length / 2; i++) {
            book.getProperties().add(kvs[2 * i] + kvs[2 * i + 1]);
        }

        book.setAvgVoteStar(doc.select(".rating_num").get(0).text());
        book.setVote(doc.getElementById("interest_sectl").text());
        StringBuilder infoSb = new StringBuilder();
        Elements infos = doc.select(".intro");
        for (int i = 0; i < infos.size(); i++) {
            Elements ps = infos.get(i).select("p");
            if (ps.text().contains("(展开全部)")) {
                continue;
            }
            for (Element p : ps) {
                if (i == (infos.size() - 1)) {
                    for (String pro : book.getProperties()) {
                        if (pro.contains("作者")) {
                            infoSb.append(pro.split(":")[1]).append(":");
                            break;
                        }
                    }
                }
                infoSb.append(p.text()).append("<br/>");
            }
        }
        book.setIntro(infoSb.toString());
        book.setMenu(doc.select("div.indent[style=display:none]").text());

        Elements comment = doc.select("div.tlst");
        Elements ilst = comment.select(".ilst");
        for (Element e : ilst) {
            book.commentUsers.add(e.select("a").attr("title"));
            //System.out.println(e.select("a").attr("title"));
        }
        Elements nlst = comment.select(".nlst");
        for (Element e : nlst) {
            book.commentTitles.add(e.select("a").get(2).attr("title"));
        }
        Elements clst = comment.select(".clst");
        for (Element e : clst) {
            book.getComments().add(e.select("span").get(3).text());
        }
        return book;

    }

    public static void main(String args[]) {
        Book book = new DoubanBookParser().parse("https://book.douban.com/subject/3558788/");
        //        System.out.println(book.getAvgVoteStar());
        //        System.out.println(book.getTitle());
        //        System.out.println(book.getVote());
        //        System.out.println(book.getComments());
        System.out.println(book.getIntro());
    }
}
