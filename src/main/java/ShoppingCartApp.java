

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.sql.*;

public class ShoppingCartApp extends AbstractVerticle {

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ShoppingCartApp());
    }


    public void getAll(RoutingContext routingContext){

        String results= "";
        DBConnection dbcon = new DBConnection();
        try {
            results = dbcon.getAllFromDb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        routingContext.response()
                .putHeader("content-type", "text/html")
                .end("Your cart: " + results);
                //.end("These items are in your cart: " + results);
    }

    public void addToCart(RoutingContext routingContext){
        if(routingContext.getBodyAsString() != null){
            JsonObject product = routingContext.getBodyAsJson();
            String name = product.getString("name");
            int price = product.getInteger("price");
            DBConnection dbcon = new DBConnection();
            String response = "";
            try {
                response = dbcon.addToDb(name, price);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "text/html")
                    .end(response);
        }
        else{
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "text/html")
                    .end("body was null");
        }
    }

    private void deleteOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        String response = "";
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer itemid = Integer.valueOf(id);
        DBConnection dbcon = new DBConnection();
            try {
                response = dbcon.deleteFromDb(itemid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        routingContext.response()
                .setStatusCode(204)
                .putHeader("content-type", "text/html")
                .end(response);
    }
    @Override
    public void start(Future<Void> future){

        Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html")
                    .end("Welcome to the shopping cart app");
        });

        router.route("/cart").handler(BodyHandler.create());

        router.get("/cart").handler(this::getAll);

        router.post("/cart").handler(this::addToCart);

        router.delete("/cart/:id").handler(this::deleteOne);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 9090),
                        result -> {
                            if(result.succeeded()){
                                future.complete();
                            }
                            else{
                                future.fail(result.cause());
                            }
                        });
    }
}
