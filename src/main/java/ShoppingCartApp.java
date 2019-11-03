
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.junit.Test;

import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShoppingCartApp extends AbstractVerticle {

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ShoppingCartApp());
    }

    private Map<Integer, Product> cart = new LinkedHashMap<>();

    private void getAll(RoutingContext routingContext){
        String results= "";
        for(int key: cart.keySet()){
            results += (cart.get(key).toString() + " ");
        }
        routingContext.response()
                .putHeader("content-type", "text/html")
                .end("These items are in your cart: " + results);
    }

    public void addToCart(RoutingContext routingContext){
        if(routingContext.getBodyAsString() != null){
            final Product product = Json.decodeValue(routingContext.getBodyAsString(), Product.class);
            cart.put(product.getId(), product);
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "text/html")
                    .end("You added an item to the cart: " + product.toString());
        }
        else{
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "text/html")
                    .end("body was null");
        }
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

        router.get("/cart/view").handler(this::getAll);

        router.post("/cart").handler(this::addToCart);

        router.delete();

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
