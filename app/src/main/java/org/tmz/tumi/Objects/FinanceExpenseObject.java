package org.tmz.tumi.Objects;

public class FinanceExpenseObject {
    String type, date, total, expenseID;

    public FinanceExpenseObject(String type, String date, String total, String expenseID) {
        this.type = type;
        this.date = date;
        this.total = total;
        this.expenseID = expenseID;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTotal() {
        return total;
    }

    public String getExpenseID() {
        return expenseID;
    }
}
