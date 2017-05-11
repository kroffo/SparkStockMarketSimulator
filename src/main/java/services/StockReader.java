package services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by kennethroffo on 5/11/17.
 */
public class StockReader {
    private static final String BASE_URL = "http://finance.google.com/finance/info?client=ig&q=NASDAQ:";
    private static long previousUpdateTime = 0;

    public static void updateStocks(String[] symbols) {
        long currentTime = System.currentTimeMillis();

        // Don't update more than once every ten seconds
        if(currentTime - previousUpdateTime > 10000)
            previousUpdateTime = currentTime;
        else
            return;

        String urlString = BASE_URL;
        URL url = null;
        HttpURLConnection connection = null;
        JSONArray arr = null;
        for(String sym : symbols)
            urlString += sym + ",";
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "text/plain");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()
                    )
            );

            String inputLine;
            String response = "";
            while ((inputLine = in.readLine()) != null) {
                response = response + inputLine + "\n";
            }
            in.close();
            if(response == null) {
                return;
            }
            // the url returns two '/' characters at the start.
            // Remove them for valid JSON.
            response = response.substring(3);
            arr = new JSONArray(response);
        } catch(IOException e) {
            throw new RuntimeException(e);
        } catch(JSONException e) {
            // This happens when the response was not as expected, likely due to fake company symbols
            // In that case, the companies don't need updates from here, so just return.
            return;
        } finally {
            connection.disconnect();
        }

        if(arr != null) {
            int length = arr.length();
            for(int i=0; i<length; ++i) {
                JSONObject obj = arr.getJSONObject(i);
                String sym = obj.getString("t");
                String[] priceStringComponents = obj.getString("l").split(",");
                String priceString = "";
                for(int j=0; j<priceStringComponents.length; ++j)
                    priceString += priceStringComponents[j];
                double price = Double.parseDouble(priceString);
                Connection conn = null;
                Statement statement = null;
                try {
                    conn = DBConnector.getConnection();
                    statement = conn.createStatement();
                    statement.executeUpdate("UPDATE Companies SET stockValue=" + price
                            + " WHERE symbol='" + sym + "';");
                } catch(SQLException | NullPointerException e) {
                    throw new RuntimeException(e);
                } finally {
                    try { statement.close(); } catch(SQLException | NullPointerException e) { }
                    try { conn.close(); } catch(SQLException | NullPointerException e) { }
                }
            }
        }
    }

}
