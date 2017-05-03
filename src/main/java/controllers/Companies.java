package controllers;

import java.util.HashMap;
import java.util.Set;

import static spark.Spark.*;
import models.Company;
import org.json.JSONException;
import org.json.JSONObject;

public class Companies {

    private static HashMap<String, Company> companies = new HashMap<>();

    public static String getCompanies(spark.Request req, spark.Response res) {
        Set<String> keySet = companies.keySet();
        String[] keys = keySet.toArray( new String[keySet.size()] );

        String json = "[\n";

        for(int i=0; i<keys.length; ++i) {
            Company c = companies.get(keys[i]);
            json += "  {\n";
            json += "    \"name\": \"" + c.getName() + "\",\n";
            json += "    \"symbol\": \"" + c.getSymbol() + "\",\n";
            json += "    \"price\": " + c.getPrice() + ",\n";
            json += "    \"stocks\": " + c.getNumberOfAvailableStocks() + "\n";
            json += "  }";
            if(i < keys.length - 1)
                json += ",";
            json += "\n";
        }

        json += "]";
        res.status(200);
        return json;
    }

    public static String getCompany(spark.Request req, spark.Response res, String symbol) {
        Company c = companies.get(symbol);
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
            res.status(404);
            return null;
        }
    }

    public static String addCompany(spark.Request req, spark.Response res) {
        JSONObject obj;
        try {
            obj = new JSONObject(req.body());
        } catch(JSONException e) {
            res.status(400);
            return "Request body must be valid JSON.";
        }
        String name = obj.getString("name");
        String symbol = obj.getString("symbol");
        if(name == null || name.equals("")) {
            res.status(400);
            return "Missing parameter \"name\".";
        }
        if(symbol == null || symbol.equals("")) {
            res.status(400);
            return "Missing parameter \"symbol\".";
        }
        Company c = companies.get(symbol);
        if(c != null) {
            res.status(409);
            return "Company with symbol \"" + symbol + "\" already exists.";
        }
        c = new Company(name, symbol);
        companies.put(symbol, c);
        res.status(201);
        return "Company \"" + symbol + "\" created.";
    }

    public static String deleteCompany(spark.Request req, spark.Response res, String symbol) {
        if(companies.get(symbol) != null) {
            res.status(200);
            companies.remove(symbol);
            return "Company \"" + symbol + "\" deleted.";
        } else {
            res.status(404);
            return null;
        }
    }

    public static String updateCompany(spark.Request req, spark.Response res, String symbol) {
        Company c = companies.get(symbol);
        if(c == null) {
            res.status(404);
            return null;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(req.body());
        } catch(JSONException e) {
            res.status(400);
            return "Request body must be valid JSON.";
        }
        String name = obj.getString("name");
        if(name == null || name.equals("")) {
            res.status(400);
            return "Missing parameter \"name\".";
        }
        c.updateName(name);
        res.status(200);
        return "Name for company \"" + symbol + "\" updated to \"" + name + ".";
    }
}
