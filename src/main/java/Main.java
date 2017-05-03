import controllers.*;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        get("/companies", (req, res) -> Companies.getCompanies(req, res));
        get("/companies/:symbol", (req, res) -> Companies.getCompany(req, res, req.params(":symbol")));
        post("/companies", (req, res) -> Companies.addCompany(req, res));
        delete("/companies/:symbol", (req, res) -> Companies.deleteCompany(req, res, req.params(":symbol")));
        put("/companies/:symbol", (req, res) -> Companies.updateCompany(req, res, req.params(":symbol")));
    }
}
