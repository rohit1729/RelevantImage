package com.infoaxe.model;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by rohitgupta on 12/24/16.
 */
public class Image implements Comparable<Image>{
    public static HashSet<String> formats = new HashSet<>(Arrays.asList("jpg","jpeg","png","svg","cms"));
    private int height;
    private int width;
    private URL url;
    private int depth;
    private static HashMap<Integer,Integer> bannerSizes= new HashMap<>();
    private Boolean isBanner = false;
    private float score;
    private int area;
    private float[] weights = {0.35f,0.4f,0.25f};
    private float squared = 0.00f;
    static {
        bannerSizes.put(468,60);
        bannerSizes.put(728,90);
        bannerSizes.put(336,280);
        bannerSizes.put(300,250);
    }

    private void setSquared() {
        if (height == 0 || width == 0) return;
        this.squared = (float) Math.min(this.getWidth(),this.getHeight())/Math.max(this.getWidth(),this.getHeight());
    }

    private void setArea() {
        this.area = this.height * this.width;
    }

    public int getArea() {
        return area;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setScore(int max_area,int max_depth,int min_depth, int min_area){
        if (isBanner || getArea() == 0) {
            score = 0.0f;
            return;
        }
        this.score = (weights[0] * squared) + (weights[1] * (max_depth-depth)/(max_depth-min_depth)) + (weights[2] * (area-min_area)/(max_area-min_area));
        RelevantLogger.log("image: "+this.getUrl().toString()+"score :"+String.valueOf(this.score));
    }


    public void setIsBanner(){
        if (height == 0 || width == 0) return;
        Iterator iterator = bannerSizes.keySet().iterator();
        if (iterator.hasNext()){
            Integer width = (Integer) iterator.next();
            Integer height = bannerSizes.get(width);

            if (Math.abs(height - this.height) < 5 && Math.abs(width - this.width) < 5){
                this.isBanner = true;
            }
        }
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    private WebDocument document;

    public WebDocument getDocument() {
        return document;
    }

    public void setDocument(WebDocument document){
        this.document = document;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.setArea();
        this.setSquared();
        this.setIsBanner();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
        this.setArea();
        this.setSquared();
        this.setIsBanner();
    }

    @Override
    public int compareTo(Image otherImage) {
        if (this.score == otherImage.score) return 0;
        if (this.score > otherImage.score) return -1;
        return 1;
    }
}
