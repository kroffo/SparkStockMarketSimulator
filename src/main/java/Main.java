import controllers.*;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        get("/companies", (req, res) -> Companies.getCompanies(req, res));
        get("/companies/:symbol", (req, res) -> Companies.getCompany(req, res, req.params(":symbol")));
        post("/companies", (req, res) -> Companies.addCompany(req, res));
        delete("/companies/:symbol", (req, res) -> Companies.deleteCompany(req, res, req.params(":symbol")));
        put("/companies/:symbol", (req, res) -> Companies.updateCompany(req, res, req.params(":symbol")));
        get("/users", (req, res) -> Users.getUsers(req, res));
        get("/users/:name", (req, res) -> Users.getUser(req, res, req.params(":name")));
        post("users", (req, res) -> Users.addUser(req, res));
        delete("users/:name", (req, res) -> Users.deleteUser(req, res, req.params(":name")));
        put("users/:name", (req, res) -> Users.updateUser(req, res, req.params(":name")));
        get("/users/:name/stocks", (req, res) -> Stocks.getStocks(req, res, req.params(":name")));
        get("/users/:name/stocks/:sym", (req, res) -> Stocks.getStock(req, res, req.params(":name"), req.params("sym")));
        post("/users/:name/stocks/:sym", (req, res) -> Stocks.performAction(req, res, req.params(":name"), req.params("sym")));
    }
}
