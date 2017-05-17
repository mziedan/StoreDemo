package com.beekoding.storedemo.Model;

/**
 * Created by moham on 2017-05-15.
 */

public class ProductImage {

    private int height;

    private int width;

    private String url;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ProductImage [height = " + height + ", width = " + width + ", url = " + url + "]";
    }

}
