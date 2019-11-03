import java.util.concurrent.atomic.AtomicInteger;

public class Product {

    private static final AtomicInteger counter = new AtomicInteger();

    private final int id;
    private String name;
    private int price;

    public Product(String name, int price){
        this.id = counter.getAndIncrement();
        this.name = name;
        this.price = price;
    }

    public Product(){
        this.id = counter.getAndIncrement();
    }

    public String getName(){

        return name;
    }

    public int getPrice(){

        return price;
    }

    public int getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(int price){
        this.price = price;
    }

    @Override
    public String toString(){
        return "Item: " + name + ", £" + price;
    }
}
