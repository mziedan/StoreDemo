package com.beekoding.storedemo.Model;

/**
 * Created by moham on 2017-05-15.
 */

public class ProductItem {
    private int id;

    private double price;

    private String productDescription;

    private ProductImage image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public ProductImage getImage() {
        return image;
    }

    public void setImage(ProductImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ProductItem [id = " + id + ", price = " + price + ", productDescription = " + productDescription + ", image = " + image + "]";
    }
}
