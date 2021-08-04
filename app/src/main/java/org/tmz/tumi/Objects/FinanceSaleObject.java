package org.tmz.tumi.Objects;

public class FinanceSaleObject {
    public String date, total, saleID;

    public FinanceSaleObject(String date,
                             String total,
                             String saleID) {
        this.date = date;
        this.saleID = saleID;
        this.total = total;
    }

    public String getSaleID() {
        return saleID;
    }

    public String getDate() {
        return date;
    }

    public String getTotal() {
        return total;
    }
}
