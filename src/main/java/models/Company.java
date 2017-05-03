package models;

/**
 * Created by kennethroffo on 5/2/17.
 */
public class Company {

    private String name;
    private String symbol;
    private int numberOfAvailableStocks;
    private double price;

    public Company(String n, String s) {
        name = n;
        symbol = s;
        numberOfAvailableStocks = 100;
        price = 50.0;
    }

    public String getName() {
        return name;
    }

    public void updateName(String n) {
        name = n;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getNumberOfAvailableStocks() {
        return numberOfAvailableStocks;
    }

    public double getPrice() {
        return price;
    }
}
