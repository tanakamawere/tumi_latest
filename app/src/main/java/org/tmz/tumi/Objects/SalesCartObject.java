package org.tmz.tumi.Objects;

public class SalesCartObject {
    String productTitle, productQuantity, productTotal, productKey;

    public SalesCartObject(String titleSales, String quantitySales, String totalSales, String keySales) {
        this.productTitle = titleSales;
        this.productQuantity = quantitySales;
        this.productTotal = totalSales;
        this.productKey = keySales;
    }

    public String getProductKey() {
        return productKey;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getProductTotal() {
        return productTotal;
    }
}
