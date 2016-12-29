package com.infoaxe.model;

/**
 * Created by rohitgupta on 12/27/16.
 */
public class RelevantThread extends Thread{
    private volatile WebDocument document;

    public  RelevantThread(Runnable r, String name){
        super(r,name);
    }

    public void setDocument(WebDocument document){
        this.document = document;
    }

    public WebDocument getDocument() {
        return document;
    }
}
