package com.infoaxe.model;

import com.sun.tools.javac.util.Pair;

/**
 * Created by rohitgupta on 12/25/16.
 */
public class UrlImagePair{
    private String url;
    private String imageUrl;
    public UrlImagePair(String url,String imageUrl) {
        this.url= url;
        this.imageUrl = imageUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
