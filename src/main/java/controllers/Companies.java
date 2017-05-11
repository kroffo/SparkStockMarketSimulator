package controllers;

import static spark.Spark.*;
import models.Company;
import org.json.JSONException;
import org.json.JSONObject;

public class Companies {

    public static String getCompanies(spark.Request req, spark.Response res) {
        Company.updatePrices();
        Company[] companies = Company.getCompanies();
        String json = "[\n";

        for(int i=0; i<companies.length; ++i) {
            Company c = companies[i];
            json += "  {\n";
            json += "    \"name\": \"" + c.getName() + "\",\n";
            json += "    \"symbol\": \"" + c.getSymbol() + "\",\n";
            json += "    \"price\": " + c.getPrice() + ",\n";
            json += "    \"stocks\": " + c.getNumberOfAvailableStocks() + "\n";
            json += "  }";
            if(i < companies.length - 1)
                json += ",";
            json += "\n";
        }

        json += "]";
        res.status(200);
        return json;
    }

    // In the below methods, return null is called after all halts to prevent compilers from thinking
    // JSON objects are not initialized when they are used.

    public static String getCompany(spark.Request req, spark.Response res, String symbol) {
        Company.updatePrices();
        Company c = Company.load(symbol);
        if(c != null) {
            res.status(200);
            String json = "{\n";
            json += "  \"name\": \"" + c.getName() + "\",\n";
            json += "  \"symbol\": \"" + c.getSymbol() + "\",\n";
            json += "  \"price\": " + c.getPrice() + ",\n";
            json += "  \"stocks\": " + c.getNumberOfAvailableStocks() + "\n";
            json += "}";
            return json;
        } else {
            halt(404, "Company with symbol \"" + symbol + "\" does not exist.");
            return null;
        }
    }

    public static String addCompany(spark.Request req, spark.Response res) {
        JSONObject obj;
        try {
            obj = new JSONObject(req.body());
        } catch(JSONException e) {
            halt(400, "Request must be valid JSON");
            return null;
        }
        String name = obj.has("name") ? obj.getString("name") : null;
        String symbol = obj.has("symbol") ? obj.getString("symbol") : null;
        if(name == null || name.equals("")) {
            halt(400, "Missing parameter \"name\".");
            return null;
        }
        if(symbol == null || symbol.equals("")) {
            halt(400, "Missing parameter \"symbol\".");
            return null;
        } else if(symbol.contains(" ")) {
            halt(400, "Parameter \"symbol\" must not contain any spaces.");
            return null;
        }
        Company c = Company.load(symbol);
        if(c != null) {
            halt(409, "Company with symbol \"" + symbol + "\" already exists.");
            return null;
        }
        c = Company.addCompany(name, symbol);
        res.status(201);
        return "Company \"" + symbol + "\" created.";
    }

    public static String deleteCompany(spark.Request req, spark.Response res, String symbol) {
        Company c = Company.load(symbol);
        if(c != null) {
            res.status(200);
            c.delete();
            return "Company \"" + symbol + "\" deleted.";
        } else {
            halt(404, "Company with symbol \"" + symbol + "\" does not exist.");
            return null;
        }
    }

    public static String updateCompany(spark.Request req, spark.Response res, String symbol) {
        Company c = Company.load(symbol);
        if(c == null) {
            halt(404, "Company with symbol \"" + symbol + "\" does not exist.");
            return null;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(req.body());
        } catch(JSONException e) {
            halt(400, "Request body must be valid JSON.");
            return null;
        }
        String name = obj.getString("name");
        if(name == null || name.equals("")) {
            halt(400, "Missing parameter \"name\".");
            return null;
        }
        c.updateName(name);
        res.status(200);
        return "Name for company \"" + symbol + "\" updated to \"" + name + "\".";
    }
}
