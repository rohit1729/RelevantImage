package com.infoaxe.network;

import com.infoaxe.model.Image;
import com.infoaxe.model.RelevantLogger;
import com.infoaxe.model.RelevantThread;
import com.infoaxe.model.WebDocument;
import com.infoaxe.relevant.Analyzer;
import com.infoaxe.relevant.FindRelevant;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by rohitgupta on 12/24/16.
 */
public class ImageGrabber implements Runnable {

    private Image image;
    private BufferedImage bufferedImage;
    private WebDocument document;
    private int retries;
    private int imagesfinished;
    public ImageGrabber(Image image, WebDocument document){
        this.image = image;
        this.document = document;
    }

    @Override
    public void run() {
        try{
            ((RelevantThread) Thread.currentThread()).setDocument(document);
            while (retries < 3){
                Boolean download = downloadImage();
                if (download){
                    return;
                } else {
                    if (retries == 3) {
                        imagesfinished = document.getImageTried().incrementAndGet();
                        startAnalyzerThread(imagesfinished);
                        return;
                    }
                }
            }
        }catch (Exception e){
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),e);
        }
    }


    public Boolean downloadImage(){
        try{
            retries++;
            RelevantLogger.imageQueueLog(retries, image.getUrl().toString());
            bufferedImage = ImageIO.read(image.getUrl());
            imagesfinished = document.getImageTried().incrementAndGet();
            if (bufferedImage == null || bufferedImage.getWidth() <= 30 || bufferedImage.getHeight() <= 30){
                startAnalyzerThread(imagesfinished);
                return true;
            }
            image.setHeight(bufferedImage.getHeight());
            image.setWidth(bufferedImage.getWidth());

            startAnalyzerThread(imagesfinished);
            return true;
        }catch (IOException e){
            e.printStackTrace();
            RelevantLogger.log("Exception thrown ImageGrabber");
            if (!(e instanceof SocketTimeoutException)) retries = 3;
            return false;
        }
    }

    public void startAnalyzerThread(int imagesfinished){
        if(imagesfinished == document.getImages().size()){
            FindRelevant.imageDownloadExecutor.submit(new Analyzer(document));
        }
    }

    public WebDocument getDocument() {
        return document;
    }
}

