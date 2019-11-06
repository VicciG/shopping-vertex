import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DBConnection {

    private String url = "jdbc:postgresql://localhost:5432/shoppingcart";
    private String user = "dbadmin";
    private String password = "dbadm!n";

    public DBConnection(){
        this.url =url;
        this.user=user;
        this.password=password;
    }

    public String getVersion(){
        String version = "";
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT VERSION()")) {

            if (rs.next()) {
                version += "Version: " + (rs.getString(1));
                con.close();
            }

        } catch (SQLException ex) {
            version += (ex.toString());
        }

        return version;
    }
    public String addToDb(String name, int price) throws SQLException {
        String resp = "";
         Connection con = DriverManager.getConnection(url, user, password);
         PreparedStatement stmt = con.prepareStatement("insert into cart(item_name, item_price) values(?,?)");
         stmt.setString(1, name);
         stmt.setInt(2, price);

         int row = stmt.executeUpdate();
         con.close();
         return "Successfully added item to DB";

    }
    public String getAllFromDb() throws SQLException{
        String resp = "";
        Connection con = DriverManager.getConnection(url, user, password);
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart");

        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            resp += "ID: " + rs.getInt("id");
            resp += "\n" +"Item: " + rs.getString("item_name");
            resp += "\n" +"Price: " + rs.getInt("item_price") + "\n";
        }
        con.close();
        return resp;
    }
    public String deleteFromDb(int id) throws SQLException{
        String resp = "";
        Connection con = DriverManager.getConnection(url, user, password);
        PreparedStatement stmt = con.prepareStatement("DELETE FROM cart WHERE id = ?");
        stmt.setInt(1, id);
        int row = stmt.executeUpdate();
        resp = "Item with id " + id + " deleted from table";
        con.close();
        return resp;
    }

}
