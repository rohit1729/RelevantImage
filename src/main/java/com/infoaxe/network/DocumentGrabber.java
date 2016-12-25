package com.infoaxe.network;

import com.infoaxe.model.Image;
import com.infoaxe.model.RelevantLogger;
import com.infoaxe.model.UrlImagePair;
import com.infoaxe.model.WebDocument;
import com.infoaxe.relevant.Analyzer;
import com.infoaxe.relevant.FindRelevant;
import com.sun.tools.javac.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by rohitgupta on 12/24/16.
 */
public class DocumentGrabber implements Callable<WebDocument>{
    private WebDocument document;
    private int retries;
    public DocumentGrabber(WebDocument document){
        this.document = document;
    }

    @Override
    public WebDocument call() throws IOException {
        WebDocument downloadDocument;
        while (retries <= 3){
            downloadDocument = download(document);
            if (downloadDocument !=null) return downloadDocument;
            else if (retries == 3){
                document.setRelevantImage("Error Retrieving");
                UrlImagePair urlImagePair  = new UrlImagePair(document.getUrl().toString(),document.getRelevantImageUrl());
                FindRelevant.relevantImageUrls.add(urlImagePair);
                return null;
            }
        }
        return null;
    }

    private WebDocument download(WebDocument document){
        try {
            retries++;
            RelevantLogger.documentQueueLog(retries, document.getUrl().toString());
            Connection.Response response= Jsoup.connect(document.getUrl().toString())
                    .ignoreContentType(true)
                    .referrer("https://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
                    .execute();
            document.setDocument(response.parse());
            if (searchMetaTags(document)) {
                UrlImagePair urlImagePair  = new UrlImagePair(document.getUrl().toString(),document.getRelevantImageUrl());
                FindRelevant.relevantImageUrls.add(urlImagePair);
                return document;
            }
            RelevantLogger.documentFinishedLog(document.getUrl().toString());
            document.fillImageElements();
            RelevantLogger.log("Filled images tree");
            int images_with_area= 0;
            for(int i =0; i < document.getImages().size() ; ++i){
                if (document.getImages().get(i).getArea() > 0) images_with_area++;
            }
            if (document.getImages().size() == 0) {
                document.setRelevantImage("None found");
                UrlImagePair urlImagePair = new UrlImagePair(document.getUrl().toString(),document.getRelevantImageUrl());
                FindRelevant.relevantImageUrls.add(urlImagePair);
                return document;
            }
            document.imageTried.getAndAdd(images_with_area);
            if (images_with_area == document.getImages().size()){
                FindRelevant.imageDownloadExecutor.submit(new Analyzer(document));
            }else {
                for (Image image : document.getImages()) {
                    if (image.getArea() == 0) {
                        FindRelevant.imageDownloadExecutor.submit(new ImageGrabber(image, document));
                    }
                }
            }
            return document;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private Boolean searchMetaTags(WebDocument document){
        Elements elements = document.getDocument().getElementsByTag("meta");
        for (Element e: elements){
            if (!e.attr("content").isEmpty() && (e.attr("property").equalsIgnoreCase("og:image") ||
                    e.attr("name").equalsIgnoreCase("twitter-image"))){
                document.setRelevantImage(e.attr("content"));
                return true;
            }
        }
        return false;
    }
}
