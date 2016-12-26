package com.infoaxe.model;

import com.infoaxe.relevant.FindRelevant;

import java.util.concurrent.ThreadFactory;

/**
 * Created by rohitgupta on 12/27/16.
 */
public class DocumentThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new RelevantThread(r,"document_thread");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                RelevantLogger.unhandledException("Document thread unhandled exception thrown");
                WebDocument document = ((RelevantThread) t).getDocument();
                FindRelevant.relevantImageUrls.add(new UrlImagePair(document.getUrl().toString(),"None, thread exception"));

            }
        });
        return thread;
    }

}
