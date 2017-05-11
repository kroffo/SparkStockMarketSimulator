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

CREATE TABLE Users (
    ID int NOT NULL AUTO_INCREMENT,
    name VARCHAR(40) UNIQUE,
    password VARCHAR(40),
    money DOUBLE PRECISION,
    PRIMARY KEY(ID)
);

CREATE TABLE Stocks (
    userName VARCHAR(40),
    companySymbol VARCHAR(10),
    stocks INT,
    averagePrice DOUBLE PRECISION,
    PRIMARY KEY(userName, companySymbol)
);

GRANT ALL ON sparkstocksim.* TO 'sparkstocksimuser' IDENTIFIED BY 'sparkstocksimpassword';
```
