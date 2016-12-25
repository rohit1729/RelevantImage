package com.infoaxe.model;

import com.infoaxe.relevant.FindRelevant;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rohitgupta on 12/24/16.
 */
public class WebDocument {

    private URL url;
    private List<Image> images = new ArrayList<Image>();
    private org.jsoup.nodes.Document document;
    public  AtomicInteger imageTried = new AtomicInteger();
    public String relevantImageUrl;

    public void setRelevantImage(String relevantImageUrl) {
        this.relevantImageUrl = relevantImageUrl;
    }

    public void fillImageElements(){
        ImageSearch imageSearch = new ImageSearch();
        this.document.traverse(imageSearch);
        images = imageSearch.getImages();
    }

    public List<Image> getImages() {
        return images;
    }

    public String getRelevantImageUrl() {
        return relevantImageUrl;
    }

    public WebDocument(URL url) {
        this.url = url;
        this.imageTried.set(0);
    }

    public void setDocument(org.jsoup.nodes.Document document){
        this.document = document;

    }

    public URL getUrl(){
        return this.url;
    }

    public org.jsoup.nodes.Document getDocument(){
        return document;
    }

    public void setImageTried(AtomicInteger imageTried) {
        this.imageTried = imageTried;
    }

    public AtomicInteger getImageTried() {
        return imageTried;
    }
}
