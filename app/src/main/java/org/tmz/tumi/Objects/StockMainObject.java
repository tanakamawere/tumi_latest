package org.tmz.tumi.Objects;

public class StockMainObject {
    String productTitle,
            productQuantity,
            productBuyingPrice,
            productSellingPrice,
            productValue,
            productKey,
            productGrossProfit;

    public StockMainObject() {
    }

    public StockMainObject(String productTitle,
                           String productQuantity,
                           String productBuyingPrice,
                           String productSellingPrice,
                           String productValue,
                           String productKey,
                           String productGrossProfit) {
        this.productTitle = productTitle;
        this.productQuantity = productQuantity;
        this.productBuyingPrice = productBuyingPrice;
        this.productSellingPrice = productSellingPrice;
        this.productValue = productValue;
        this.productKey = productKey;
        this.productGrossProfit = productGrossProfit;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductBuyingPrice() {
        return productBuyingPrice;
    }

    public void setProductBuyingPrice(String productBuyingPrice) {
        this.productBuyingPrice = productBuyingPrice;
    }

    public String getProductSellingPrice() {
        return productSellingPrice;
    }

    public void setProductSellingPrice(String productSellingPrice) {
        this.productSellingPrice = productSellingPrice;
    }

    public String getProductValue() {
        return productValue;
    }

    public void setProductValue(String productValue) {
        this.productValue = productValue;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductGrossProfit() {
        return productGrossProfit;
    }

    public void setProductGrossProfit(String productGrossProfit) {
        this.productGrossProfit = productGrossProfit;
    }
}
