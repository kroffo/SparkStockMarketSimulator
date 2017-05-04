# SparkStockMarketSimulator

SQL code for setup:
```
CREATE DATABASE sparkstocksim;
USE sparkstocksim;
CREATE TABLE  Companies (
    ID int NOT NULL AUTO_INCREMENT,
    name VARCHAR(40),
    symbol VARCHAR(10) UNIQUE,
    stockValue DOUBLE PRECISION,
    availableStocks INT,
    PRIMARY KEY(ID)
);
```
