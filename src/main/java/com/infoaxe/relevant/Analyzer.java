package com.infoaxe.relevant;

import com.infoaxe.model.Image;
import com.infoaxe.model.RelevantLogger;
import com.infoaxe.model.UrlImagePair;
import com.infoaxe.model.WebDocument;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Created by rohitgupta on 12/24/16.
 */
public class Analyzer implements Callable<Boolean> {
private int max_area;
private int max_depth;
private int min_depth;
private int min_area;
    private WebDocument document;
    public Analyzer(WebDocument document){
        this.document = document;
    }
    @Override
    public Boolean call(){
        try {
            RelevantLogger.analyzerCalled(document.getUrl().toString());
            for (int i = 0; i < document.getImages().size() ; i++){
                Image image = document.getImages().get(i);
                if (i == 0) {
                    initialze(image.getArea(), image.getDepth());
                    continue;
                }
                if (image.getDepth() > this.max_depth) {
                    this.max_depth = image.getDepth();
                }else if(image.getDepth() < this.min_depth) {
                    this.min_depth = image.getDepth();
                }

                if (image.getArea() > this.max_area) {
                    this.max_area = image.getArea();
                }else if(image.getArea() < this.min_area){
                    this.min_area = image.getArea();
                }
            }
            Image mostRelevantImage = mostRelevantImage();
            document.setRelevantImage(mostRelevantImage.getUrl().toString());
            UrlImagePair urlImagePair  = new UrlImagePair(document.getUrl().toString(),document.getRelevantImageUrl());
            FindRelevant.relevantImageUrls.add(urlImagePair);
            RelevantLogger.analyzerFinished(document.getUrl().toString());
            return true;
        }catch (Exception e){
            RelevantLogger.log("Analyzer exception");
            return false;
        }
    }

    public void initialze(int area, int depth){
        this.max_area = area;
        this.max_depth = depth;
        this.min_depth= depth;
        this.max_area = area;
    }

    public Image mostRelevantImage(){
        float max_score = -1.0f;
        Image mostRelevantImage = null;
        for (Image i : document.getImages()){
            i.setScore(this.max_area,this.max_depth,this.min_depth,this.min_area);
            if (max_score == -1.0f) {
                max_score = i.getScore();
                mostRelevantImage = i;
            }else {
                if (i.getScore() > max_score){
                    max_score = i.getScore();
                    mostRelevantImage = i;
                }
            }
        }
        return mostRelevantImage;
    }

}
