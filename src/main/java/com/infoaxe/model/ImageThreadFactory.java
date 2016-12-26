package com.infoaxe.model;

import com.infoaxe.relevant.Analyzer;
import com.infoaxe.relevant.FindRelevant;

import java.util.concurrent.ThreadFactory;

public class ImageThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new RelevantThread(r,"image_document");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                RelevantLogger.unhandledException("Image thread unhandled exception thrown");
                WebDocument document = ((RelevantThread) t).getDocument();
                int img_done = document.imageTried.incrementAndGet();
                if (img_done == document.getImages().size()){
                    FindRelevant.imageDownloadExecutor.submit(new Analyzer(document));
                }
            }
        });
        return thread;
    }

}

