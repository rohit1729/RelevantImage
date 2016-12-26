package com.infoaxe.model;


import com.infoaxe.relevant.FindRelevant;

import java.util.logging.Logger;

/**
 * Created by rohitgupta on 12/26/16.
 */
public class RelevantLogger {
    private static final Logger mLogger = Logger.getLogger(RelevantLogger.class.getName());

    public static void imageQueueLog(int retries, String url){
        mLogger.info("Queuing image download "+retries+" :"+url);
    }

    public static void imageFinishedLog(int finished,int total, String url){
        mLogger.info("ImageGrabber finished "+finished+"/"+total+" :"+url);
    }

    public static void doucmentDoneLog(int finished, int total){
        mLogger.info("Document Done "+finished+"/"+total);
    }

    public static void documentFinishedLog(String url){
        mLogger.info("Document downloaded :"+url);
    }

    public static void analyzerFinished(String url){
        mLogger.info("Analyzer finished :"+url);
    }

    public static void log(String message){
        mLogger.info(message);
    }

    public static void analyzerCalled(String url){
        mLogger.info("Analyzer called :"+url);
    }

    public static void documentQueueLog(int retries, String url){
        mLogger.info("Queuing document download "+retries+" :"+url);
    }

    public static void unhandledException(String message){
        mLogger.warning(message);
    }
}
