package org.tmz.tumi.Objects;

public class SaleDetailObject {
    String customerName, customerPhone, date, total;

    public SaleDetailObject() {
    }

    public SaleDetailObject(String customerName, String customerPhone, String date, String total) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.date = date;
        this.total = total;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getDate() {
        return date;
    }

    public String getTotal() {
        return total;
    }
}
