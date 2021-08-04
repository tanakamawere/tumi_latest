package org.tmz.tumi.Objects;

public class DisplayProductsObject {
    String productTitle, productQuantity, productPrice, productTotal, productKey;

    public DisplayProductsObject(
            String title,
            String quantity,
            String price,
            String total,
            String key) {
        this.productTitle = title;
        this.productQuantity = quantity;
        this.productPrice = price;
        this.productTotal = total;
        this.productKey = key;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductTotal() {
        return productTotal;
    }

    public String getProductKey() {
        return productKey;
    }
}
