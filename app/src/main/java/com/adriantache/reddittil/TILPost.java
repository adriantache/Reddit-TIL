package com.adriantache.reddittil;

public class TILPost {

    private String id;
    private String title;
    private String url;
    private long time;

    public TILPost(String id, String title, String url, long time) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
