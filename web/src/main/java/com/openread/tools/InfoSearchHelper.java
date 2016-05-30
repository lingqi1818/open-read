package com.openread.tools;

import java.util.List;

public class InfoSearchHelper {

    public static enum InfoType {
        JAVA,
        CPP,
        OS,
        DATABASE,
        KV,
        SEARCH,
        CACHE,
        BIGDATA,
        SCRIPT,
        ARCHITECTURE,
        MOBILE,
        NEWS
    }

    public static interface InfoSpider {
        public List<Info> craw(int num);
    }

    public static class Info {
        private InfoType type;
        private String   title;
        private String   surl;
        private String   author;
        private String   context;
        private String   createTime;
        private String   img;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public InfoType getType() {
            return type;
        }

        public void setType(InfoType type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSurl() {
            return surl;
        }

        public void setSurl(String surl) {
            this.surl = surl;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
