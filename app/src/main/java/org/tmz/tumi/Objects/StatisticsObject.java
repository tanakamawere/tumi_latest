package org.tmz.tumi.Objects;

public class StatisticsObject {
    String Sales, Expenses, Debts, Credits, Time, Date, Key, Profit;

    public StatisticsObject(String sales, String expenses, String debts, String credits, String time, String date, String key, String profit) {
        Sales = sales;
        Expenses = expenses;
        Debts = debts;
        Credits = credits;
        Time = time;
        Date = date;
        Key = key;
        Profit = profit;
    }

    public String getSales() {
        return Sales;
    }

    public String getExpenses() {
        return Expenses;
    }

    public String getDebts() {
        return Debts;
    }

    public String getCredits() {
        return Credits;
    }

    public String getTime() {
        return Time;
    }

    public String getDate() {
        return Date;
    }

    public String getKey() {
        return Key;
    }

    public String getProfit() {
        return Profit;
    }
}
