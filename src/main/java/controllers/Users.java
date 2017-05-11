package controllers;

import static spark.Spark.*;
import models.User;
import org.json.JSONException;
import org.json.JSONObject;

public class Users {

    public static String getUsers(spark.Request req, spark.Response res) {

        User[] users = User.getUsers();
        String json = "[\n";

        for(int i=0; i < users.length; ++i) {
            User u = users[i];
            json += "  {\n";
            json += "    \"name\": \"" + u.getName() + "\",\n";
            json += "    \"password\": \"" + u.getPassword() + "\",\n";
            json += "    \"money\": " + String.format( "%.2f", u.getMoney()) + "\n";
            json += "  }";
            if(i < users.length - 1)
                json += ",";
            json += "\n";
        }

        json += "]";
        res.status(200);
        return json;
    }

    public static String getUser(spark.Request req, spark.Response res, String name) {
        User u = User.load(name);
        if(u != null) {
            res.status(200);
            String json = "{\n";
            json += "  \"name\": \"" + u.getName() + "\",\n";
            json += "  \"password\": \"" + u.getPassword() + "\",\n";
            json += "  \"money\": " + String.format( "%.2f", u.getMoney()) + "\n";
            json += "}";
            return json;
        } else {
            halt(404, "User with name \"" + name + "\" does not exist.");
            return null;
        }
    }

    public static String addUser(spark.Request req, spark.Response res) {
        JSONObject obj;
        try {
            obj = new JSONObject(req.body());
        } catch(JSONException e) {
            halt(400, "Request must be valid JSON");
            return null;
        }
        String name = obj.has("name") ? obj.getString("name") : null;
        String password = obj.has("password") ? obj.getString("password") : null;
        if(name == null || name.equals("")) {
            halt(400, "Missing parameter \"name\".");
            return null;
        }
        if(password == null || password.equals("")) {
            halt(400, "Missing parameter \"password\".");
            return null;
        }
        User u = User.load(name);
        if(u != null) {
            halt(409, "User with the name \"" + name + "\" already exists.");
            return null;
        }
        u = User.addUser(name, password);
        res.status(201);
        return "User \"" + name + "\" created.";
    }

    public static String deleteUser(spark.Request req, spark.Response res, String name) {
        User u = User.load(name);
        if(u != null) {
            res.status(200);
            u.delete();
            return "User \"" + name + "\" deleted.";
        } else {
            halt(404, "User with name \"" + name + "\" does not exist");
            return null;
        }
    }

    public static String updateUser(spark.Request req, spark.Response res, String name) {
        User u = User.load(name);
        if(u == null) {
            halt(404, "User with name \"" + name + "\" does not exist.");
            return null;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(req.body());
        } catch(JSONException e) {
            halt(400, "Request body must be valid JSON.");
            return null;
        }
        String password = obj.getString("password");
        if(password == null || password.equals("")) {
            halt(400, "Missing parameter \"password\"");
            return null;
        }
        u.updatePassword(password);
        res.status(200);
        return "Password for user \"" + name + "\" updated to \"" + password + "\".";
    }
}
