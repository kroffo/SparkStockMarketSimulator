package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import services.*;

/**
 * Created by kennethroffo on 5/2/17.
 */
public class Company {
    private static final int DEFAULT_NUMBER_OF_STOCKS = 100;
    private static final double DEFAULT_STOCK_VALUE = 50.0;

    private String name;
    private String symbol;
    private int numberOfAvailableStocks;
    private double price;

    public static Company addCompany(String n, String s) {
        Company c = new Company(n, s, DEFAULT_NUMBER_OF_STOCKS, DEFAULT_STOCK_VALUE);
        c.save();
        return c;
    }

    private Company(String n, String s, int ns, double p) {
        name = n;
        symbol = s;
        numberOfAvailableStocks = ns;
        price = p;
    }

    public String getName() {
        return name;
    }

    public void updateName(String n) {
        name = n;
        this.update();
    }

    public String getSymbol() {
        return symbol;
    }

    public int getNumberOfAvailableStocks() {
        return numberOfAvailableStocks;
    }

    public void removeStock() {
        --numberOfAvailableStocks;
    }

    public void addStock() {
        ++numberOfAvailableStocks;
    }

    public double getPrice() {
        return price;
    }

    // Inserts a company into the database
    private boolean save() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
        ) {
            statement.executeUpdate("INSERT INTO Companies (name, symbol, stockValue, availableStocks) Values('"
                    + this.name + "', '"
                    + this.symbol + "', "
                    + this.price + ", "
                    + this.numberOfAvailableStocks + ");"
            );
        } catch(SQLException e) {
            return false;
        }
        return true;
    }

    // Update a company's stored data
    public boolean update() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
        ) {
            statement.executeUpdate("UPDATE Companies SET "
                    + "name='" + this.name + "', "
                    + "stockValue=" + this.price + ", "
                    + "availableStocks=" + this.numberOfAvailableStocks + " "
                    + "WHERE symbol='" + this.symbol + "';"
            );
        } catch(SQLException e) {
            return false;
        }
        return true;
    }

    // Delete a company from the db
    public boolean delete() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
        ) {
            statement.executeUpdate("DELETE FROM Companies WHERE symbol='" + this.symbol + "'");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public static Company load(String symbol) {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM Companies WHERE symbol='"+ symbol + "';");
        ) {
            if(rs.next()) {
                String name = rs.getString("name");
                double sv = rs.getDouble("stockValue");
                int as = rs.getInt("availableStocks");
                return new Company(name, symbol, as, sv);
            } else
                return null;
        } catch(SQLException e) {
            return null;
        }
    }

    public static Company[] getCompanies() {
        ArrayList<Company> companies = new ArrayList<>();
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM Companies;");
        ) {
            while(rs.next()) {
                String n = rs.getString("name");
                String sym = rs.getString("symbol");
                double sv = rs.getDouble("stockValue");
                int as = rs.getInt("availableStocks");
                companies.add(new Company(n, sym, as, sv));
            }
            return companies.toArray(new Company[companies.size()]);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updatePrices() {
        Company[] companies = getCompanies();
        int length = companies.length;
        String[] symbols = new String[length];
        for(int i=0; i<length; ++i)
            symbols[i] = companies[i].getSymbol();
        StockReader.updateStocks(symbols);
    }
}
