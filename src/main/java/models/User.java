package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import services.*;

public class User {
    private static final double DEFAULT_STARTING_MONEY = 5000.0;

    private String name;
    private String password;
    private double money;

    public static User addUser(String n, String p) {
        User u = new User(n, p, DEFAULT_STARTING_MONEY);
        u.save();
        return u;
    }

    private User(String n, String p, double m) {
        name = n;
        password = p;
        money = m;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public double getMoney() {
        return money;
    }

    public void updatePassword(String p) {
        password = p;
        this.update();
    }

    private boolean save() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ) {
            statement.executeUpdate("INSERT INTO Users (name, password, money) VALUES('"
            + this.name + "','"
            + this.password + "',"
                            +this.money + ");"
            );
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean update() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ) {
            statement.executeUpdate("UPDATE Users SET "
            + "password='" + this.password + "' "
            + "WHERE name='" + this.name + "';"
            );
        } catch(SQLException e) {
            return false;
        }
        return true;
    }

    public boolean delete() {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ) {
            statement.executeUpdate("DELETE FROM Users WHERE name='" + this.name + "'");
        } catch(SQLException e) {
            return false;
        }
        return true;
    }

    public static User load(String name) {
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM Users WHERE name = '" + name + "';");
                ) {
            if(rs.next()) {
                String password = rs.getString("password");
                double money = rs.getDouble("money");
                return new User(name, password, money);
            } else {
                return null;
            }
        } catch(SQLException e) {
            return null;
        }
    }

    public static User[] getUsers() {
        ArrayList<User> users = new ArrayList<>();
        try (
                Connection conn = DBConnector.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM Users;");
        ) {
            while(rs.next()) {
                String n = rs.getString("name");
                String p = rs.getString("password");
                double m = rs.getDouble("money");
                users.add(new User(n, p, m));
            }
            return users.toArray(new User[users.size()]);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
