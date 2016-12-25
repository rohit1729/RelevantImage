package com.infoaxe.model;

import com.infoaxe.relevant.FindRelevant;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rohitgupta on 12/25/16.
 */
public class ImageSearch implements NodeVisitor{
    private List<Image> images = new ArrayList<>();
    @Override
    public void head(Node node, int depth) {
        if(node instanceof Element){
            Element element = (Element) node;
            if (element.tagName().equalsIgnoreCase("img")){
                if(element.attr("src").isEmpty()) return;
                String url;
                if (Pattern.compile("^https?").matcher(element.attr("src")).find()){
                    url = element.attr("src");
                }else {
                    url = element.absUrl("src");
                }
                if (url.length() > 3 && Image.formats.contains(url.substring(url.length()-3))){
                    try{
                        Image image = new Image();
                        image.setUrl(new URL(url));
                        int[] width_height = width_height(element);
                        if (thresholdWidthHeight(width_height)){
                            image.setHeight(width_height[1]);
                            image.setWidth(width_height[0]);
                        }
                        image.setDepth(depth);
                        images.add(image);
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public List<Image> getImages() {
        return images;
    }

    @Override
    public void tail(Node node, int depth) {

    }

    private int[] width_height(Element element){
        int[] width_height = new int[2];
        width_height[0] = dimensionAttribute("width",element);
        width_height[1] = dimensionAttribute("height",element);
        return width_height;
    }

    private Boolean thresholdWidthHeight(int[] width_height){
        if (width_height[0] > 30 && width_height[1] > 30) return true;
        return false;
    }

    private int dimensionAttribute(String attributeName, Element e){
        try {
            String regex = "\\s*px;?";
            Pattern pattern = Pattern.compile(regex);
            if (e.attr(attributeName).isEmpty()) return 0;
            Matcher matcher = pattern.matcher(e.attr(attributeName));
            if (matcher.find()){
                return Integer.parseInt(e.attr(attributeName).substring(0,matcher.start()));
            }else {
                return Integer.parseInt(e.attr(attributeName));
            }
        }catch (NumberFormatException ex){
            ex.printStackTrace();
            return 0;
        }
    }
}
