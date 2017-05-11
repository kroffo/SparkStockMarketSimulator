package models;

import services.DBConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by kennethroffo on 5/11/17.
 */
public class Stock {
    private String companySymbol;
    private String userName;
    private int numberStocks;
    private double averagePrice;

    public static Stock addStock(String name, String symbol) {
        Stock s = new Stock(name, symbol, 0, 0.0);
        s.save();
        return s;
    }

    private Stock(String name, String symbol, int stocks, double avgPrice) {
        companySymbol = symbol;
        userName = name;
        numberStocks = stocks;
        averagePrice = avgPrice;
    }

    public String getUserName() {
        return userName;
    }

    public String getCompanySymbol() {
        return companySymbol;
    }

    public int getNumberStocks() {
        return numberStocks;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void buyStock(double price) {
        averagePrice = ( averagePrice * numberStocks + price ) / ++numberStocks;
    }

    public void sellStock() {
        if(numberStocks == 0) {
            return;
        } else if(--numberStocks == 0) {
            averagePrice = 0.0;
        }
    }

    // Inserts a Stock into the database
    private boolean save() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
        ) {
            statement.executeUpdate("INSERT INTO Stocks (userName, companySymbol, stocks, averagePrice) Values('"
                    + this.userName + "', '"
                    + this.companySymbol + "', "
                    + this.numberStocks + ", "
                    + this.averagePrice + ");"
            );
        } catch(SQLException e) {
            return false;
        }
        return true;
    }

    // Update a Stock's stored data
    public boolean update() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
        ) {
            statement.executeUpdate("UPDATE Stocks SET "
                    + "stocks=" + this.numberStocks + ", "
                    + "averagePrice=" + this.averagePrice + " "
                    + "WHERE userName='" + this.userName + "' AND companySymbol='" + this.companySymbol + "';"
            );
        } catch(SQLException e) {
            return false;
        }
        return true;
    }

    public static Stock load(String name, String symbol) {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM Stocks WHERE userName='" + name + "' AND companySymbol='" + symbol + "';");
        ) {
            if(rs.next()) {
                int stocks = rs.getInt("stocks");
                double avgPrice = rs.getDouble("averagePrice");
                return new Stock(name, symbol, stocks, avgPrice);
            } else
                return null;
        } catch(SQLException e) {
            return null;
        }
    }

    // Delete a Stock from the db
    public boolean delete() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
        ) {
            statement.executeUpdate("DELETE FROM Stocks WHERE userName='" + this.userName + "' AND companySymbol='"+ this.companySymbol + "'");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public static Stock[] loadStocksForUser(String name) {
        ArrayList<Stock> stockList = new ArrayList<>();
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM Stocks WHERE userName='" + name + "';");
        ) {
            while(rs.next()) {
                String symbol = rs.getString("companySymbol");
                int stocks = rs.getInt("stocks");
                double avgPrice = rs.getDouble("averagePrice");
                stockList.add( new Stock(name, symbol, stocks, avgPrice) );
            }
        } catch(SQLException e) {
            return null;
        }
        return stockList.toArray( new Stock[ stockList.size() ] );
    }
}
