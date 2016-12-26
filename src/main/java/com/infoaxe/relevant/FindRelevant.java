package com.infoaxe.relevant;
import com.infoaxe.model.RelevantLogger;
import com.infoaxe.model.UrlImagePair;
import com.infoaxe.model.WebDocument;
import com.infoaxe.network.DocumentGrabber;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FindRelevant {
    private static String[] schemes = {"http","https"};
    private static UrlValidator urlValidator = new UrlValidator(schemes);
    public static ExecutorService threadPoolExecuter;
    public static ExecutorService imageDownloadExecutor;
    private static List<String> urls = new ArrayList<>();
    public static CopyOnWriteArrayList<UrlImagePair> relevantImageUrls = new CopyOnWriteArrayList<>();
    private static BufferedWriter bw;
    private int stuck = 0;


    public static void main(String[] args)
    {
        ParseUrlFile();
        threadPoolExecuter = new ThreadPoolExecutor(5,8,1000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
        imageDownloadExecutor = new ThreadPoolExecutor(10,15,1000,TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (String url : urls){
            try {
                WebDocument webDocument = new WebDocument(new URL(url));
                DocumentGrabber documentGrabber = new DocumentGrabber(webDocument);
                threadPoolExecuter.submit(documentGrabber);
            }catch (MalformedURLException e){
                e.printStackTrace();
            }
        }
        int num = relevantImageUrls.size();
        while (num != urls.size()){
            try {
                Thread.sleep(5000);
                int temp = relevantImageUrls.size();
                WriteToFile(num, temp);
                num = temp;
                RelevantLogger.doucmentDoneLog(num,urls.size());
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        RelevantLogger.log("All files downloaded");
        if (bw != null) {
            try {
                writeHeaderFooter(bw,false);
                bw.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        threadPoolExecuter.shutdown();
        imageDownloadExecutor.shutdown();
    }

    private static void WriteToFile(int start, int end){
        try {
            if (start == end) return;
            if (bw == null) bw = new BufferedWriter(new FileWriter("img_url.html"));
            if (start == 0) writeHeaderFooter(bw,true);
            for (int i=start; i <= end-1; ++i){
                UrlImagePair pair = relevantImageUrls.get(i);
                bw.write("<a href="+pair.getUrl()+">"+pair.getUrl()+"</a>");
                bw.write("</br>");
                bw.write("<img src="+pair.getImageUrl()+"></img");
                bw.write("</br> </br> </br>");
            }
            bw.flush();
        }catch (IOException e){
            e.printStackTrace();

        }
    }

    public static void writeHeaderFooter(BufferedWriter bw, Boolean header){
        try{
            if (header){
                bw.write("<HTML>");
                bw.newLine();
                bw.write("<Body>");
            }else {
                bw.write("</Body>");
                bw.newLine();
                bw.write("</HTML>");
            }
        }catch (IOException e){

        }
    }

    public static void ParseUrlFile(){
        try {
            File file = new File("urls.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                if (urlValidator.isValid(line)){
                    urls.add(line);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
