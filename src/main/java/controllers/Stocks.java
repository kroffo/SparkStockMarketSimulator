package controllers;

import models.Company;
import models.Stock;
import models.User;
import org.json.JSONException;
import org.json.JSONObject;

import static spark.Spark.halt;

/**
 * Created by kennethroffo on 5/11/17.
 */
public class Stocks {

    public static String getStocks(spark.Request req, spark.Response res, String name) {
        if (User.load(name) == null) {
            halt(404, "User with name \"" + name + "\" does not exist.");
            return null;
        }
        Stock[] stocks = Stock.loadStocksForUser(name);
        String json = "{\n";
        if(stocks != null) {
            for(int i=0; i<stocks.length; ++i) {
                Stock stock = stocks[i];
                json += "  \"" + stock.getCompanySymbol() + "\": {\n";
                json += "    \"stocks\": " + stock.getNumberStocks() + ",\n";
                json += "    \"averagePrice\": " + stock.getAveragePrice() + "\n";
                json += "  }";
                if(i < stocks.length-1)
                    json += ",";
                json +="\n";
            }
        }
        json += "}";
        res.status(200);
        return json;
    }

    public static String getStock(spark.Request req, spark.Response res, String name, String symbol) {
        if (User.load(name) == null) {
            halt(404, "User with name \"" + name + "\" does not exist.");
            return null;
        } else if(Company.load(symbol) == null) {
            halt(404, "Company with symbol \"" + symbol + "\" does not exist.");
            return null;
        }
        Stock stock = Stock.load(name, symbol);
        if(stock == null) {
            halt(404, "User with name \"" + name + "\" has no stocks in company with symbol \"" + symbol + "\".");
            return null;
        }
        String json = "{\n";
        json += "  \"stocks\": " + stock.getNumberStocks() + ",\n";
        json += "  \"averagePrice\": " + stock.getAveragePrice() + ",\n";
        json += "}";
        return json;
    }

    public static String performAction(spark.Request req, spark.Response res, String name, String symbol) {
        User user = User.load(name);
        Company company = Company.load(symbol);
        if (user == null) {
            halt(404, "User with name \"" + name + "\" does not exist.");
            return null;
        } else if(company == null) {
            halt(404, "Company with symbol \"" + symbol + "\" does not exist.");
            return null;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(req.body());
        } catch(JSONException e) {
            halt(400, "Request must be valid JSON");
            return null;
        }
        String action = obj.has("action") ? obj.getString("action") : null;
        if(action == null) {
            halt(400, "Missing parameter \"action\"");
            return null;
        } else if(action.equals("buy")) {
            res.status(200);
            Stock stock = Stock.load(name, symbol);
            if(stock == null) {
                stock = Stock.addStock(name, symbol);
            }
            double price = company.getPrice();
            if(user.getMoney() >= price && company.getNumberOfAvailableStocks() > 0) {
                company.removeStock();
                user.purchase(price);
                stock.buyStock(price);
                company.update();
                user.update();
                stock.update();
                return "{ \"status\": \"success\" }";
            }
            return "{ \"status\": \"failed\" }";
        } else if(action.equals("sell")) {
            res.status(200);
            Stock stock = Stock.load(name, symbol);
            if(stock == null || stock.getNumberStocks() < 1)
                return "{ \"status\": \"failed\" }";
            company.addStock();
            user.sell(company.getPrice());
            stock.sellStock();
            company.update();
            user.update();
            if(stock.getNumberStocks() < 1)
                stock.delete();
            else
                stock.update();
            return "{ \"status\": \"success\" }";
        } else {
            halt(400, "Parameter \"action\" must be either \"buy\" or \"sell\"");
            return null;
        }
    }
}
