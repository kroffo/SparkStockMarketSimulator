package services;

import java.sql.*;

public class DBConnector {

    public static Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(
                    // url contains parameters to prevent SSL errors with mysql.
                    "jdbc:mysql://localhost:3306/sparkstocksim?autoReconnect=true&useSSL=false"
                    ,"sparkstocksimuser","sparkstocksimpassword"
            );
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}