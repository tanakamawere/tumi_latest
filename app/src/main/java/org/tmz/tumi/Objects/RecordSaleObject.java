package org.tmz.tumi.Objects;

public class RecordSaleObject {
    String productTitle;
    String productQuantity;

    public RecordSaleObject(String title,
                            String quantity) {
        this.productTitle = title;
        this.productQuantity = quantity;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductQuantity() {
        return productQuantity;
    }
}
