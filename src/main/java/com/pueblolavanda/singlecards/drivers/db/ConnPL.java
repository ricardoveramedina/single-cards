package com.pueblolavanda.singlecards.drivers.db;

import com.pueblolavanda.singlecards.cases.CSVLoaderResponseModel;
import com.pueblolavanda.singlecards.cases.ProductResponseModel;
import com.pueblolavanda.singlecards.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.pueblolavanda.singlecards.StringHelper.getLanguage;

@Component
public class ConnPL {



    private static final String CONNECTION_STRING = "";

    private static final String USERNAME = "";
    private static final String PASS = "";


    private Connection connection = null;
    private Statement command;
    private ResultSet rs = null;

    @Autowired
    public ConnPL(){
        createConnection(CONNECTION_STRING,USERNAME,PASS);
    }



    public List<Product> currentCardInStock(){

        List<Product> products = new ArrayList<>();
        Product product;
        ResultSet rs = queryMagicCard();

        try {

            while (rs.next()) {
                product = new Product();
                product.setSku(rs.getString("sku"));
                product.setPrecioNormal(rs.getBigDecimal("meta_value"));
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if(e.equals(java.sql.SQLNonTransientConnectionException.class)){
                System.out.println("connection error");
                createConnection(CONNECTION_STRING,USERNAME,PASS);
            }
        }

        return products;
    }
    //TODO: set retry n times
    private void createConnection(String connectionString, String username, String password){

        try {
            connection = DriverManager.getConnection(connectionString, username, password);
            command = connection.createStatement();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            if(e.equals(java.sql.SQLNonTransientConnectionException.class)){
                System.out.println("connection error");
                createConnection(CONNECTION_STRING,USERNAME,PASS);
            }
        }

    }

    public boolean updateMagicCardPrice(ProductResponseModel productResponseModel){

        BigDecimal price = productResponseModel.getProduct().getPrecioNormal();
        String sku = productResponseModel.getProduct().getSku();

        String sqlUpdate = "UPDATE wp_postmeta AS T1, " +
                "      (SELECT wp_posts.id " +
                "FROM wp_postmeta " +
                "INNER JOIN wp_posts ON wp_postmeta.post_id = wp_posts.id " +
                "WHERE (meta_value= ? )) AS T2  " +
                "  SET T1.meta_value = ? " +
                "WHERE T2.id = T1.post_id and (T1.meta_key='_price' or T1.meta_key='_regular_price')";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlUpdate);
            statement.setString(1, sku);
            statement.setBigDecimal(2, price);
            statement.executeUpdate();
            connection.commit();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


    private ResultSet queryMagicCard(){

        String query = "SELECT sku, post_id, meta_value, stock FROM wp_posts p, wp_postmeta pm,\n" +
                "(SELECT post_id as id, meta_value as sku FROM wp_postmeta pm  where meta_key='_sku' and meta_value like \"M-%\")\n" +
                "as sku_table,\n" +
                "(SELECT post_id as id, meta_value as stock FROM wp_postmeta pm  where meta_key='_stock' and (meta_value != \"0\" || meta_value != null) )\n" +
                "as stock_table\n" +
                "WHERE p.id=sku_table.id and p.id=stock_table.id and pm.post_id = sku_table.id and pm.meta_key = '_price' ORDER BY stock";

        try {
            rs = command.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rs;
    }




    //TODO: before insert check if the rows exists;


    public boolean insertYoastSeo(CSVLoaderResponseModel csvLoaderModel){

        List<Product> products = csvLoaderModel.getProducts();

        String sqlInsert;
        for(Product product : products) {
            int id = queryId(product.getSku());
            sqlInsert = "INSERT INTO wp_postmeta values" +
                    "(null,?,'_yoast_wpseo_primary_product_cat','')," +
                    "(null,?,'_yoast_wpseo_focuskw_text_input',?),"+
                    "(null,?,'_yoast_wpseo_focuskw',?)," +
                    "(null,?,'_yoast_wpseo_linkdex',24)," +
                    "(null,?,'_yoast_wpseo_content_score',90)," +
                    "(null,?,'_yoast_wpseo_metadesc',?)";
            String title_kw = product.getNombre();
            String metaDescript = "Cartas Sueltas Magic: " + product.getDescripcionCorta();
            try {
                PreparedStatement statement = connection.prepareStatement(sqlInsert);
                statement.setInt(1, id);
                statement.setInt(2, id);
                statement.setString(3,title_kw);
                statement.setInt(4, id);
                statement.setString(5,title_kw);
                statement.setInt(6, id);
                statement.setInt(7, id);
                statement.setInt(8, id);
                statement.setString(9,metaDescript);
                statement.executeUpdate();
                connection.commit();
                statement.close();
                System.out.println("Card: "+ id + " updated");
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        }

        return true;
    }

    private ResultSet querySKU(String skuCode) {

        String query = "SELECT sku, post_title, meta_value FROM wp_posts p, wp_postmeta pm, " +
                "(SELECT post_id as id, meta_value as sku FROM wp_postmeta pm  where meta_key='_sku' and meta_value = '"
                + skuCode + "') as sku_table WHERE p.id=sku_table.id and pm.post_id = sku_table.id and pm.meta_key = '_price' " +
                "GROUP BY sku ORDER BY sku";
        try {
            rs = command.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rs;
    }

    private int queryId(String skuCode){
        String query = "SELECT post_id as id FROM wp_postmeta pm  where meta_key='_sku' " +
                "and meta_value = '" + skuCode + "'";
        try {
            rs = command.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int id = 0;
        try {
            rs.next();
            id = rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public boolean checkConnection(){

        System.out.println("-------- MySQL JDBC Connection Testing ------------");

        try {
            //Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
        }

        System.out.println("MySQL JDBC Driver Registered!");

        try {
            connection = DriverManager.getConnection(CONNECTION_STRING,USERNAME,PASS);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        if (connection != null) {
            System.out.print("You made it, take control your database now!");
            return true;
        } else {
            System.out.print("Failed to make connection!");
            return false;
        }

    }


    public boolean updateShortDescription(String languageCode,String condition) {

        ResultSet rs  = productsShortdescriptionAll(languageCode,condition);

        try {
            while (rs.next()) {
                //System.out.println(rs.getString("post_id") + " - " + rs.getString("sku") + " - " + rs.getString("post_excerpt"));
                String language = getLanguage(languageCode.toUpperCase());
                String shortDescription = rs.getString("post_excerpt") + " - " + condition + " - " + language;
                System.out.println(rs.getString("post_id") + " - " + rs.getString("sku") + " - " + shortDescription);
                sqlUpdShortDesc(rs.getString("post_id"),shortDescription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private ResultSet productsShortdescriptionAll(String languageCode, String condition){

        String query = "SELECT post_id, sku, post_excerpt FROM wp_posts p, wp_postmeta pm, \n" +
                "(SELECT post_id as pId, meta_value as sku FROM wp_postmeta pm  where meta_key='_sku' and meta_value like \"M-%" + condition +"%"+languageCode+"%\")\n" +
                "as sku_table,\n" +
                "(SELECT post_id as pId, meta_value as stock FROM wp_postmeta pm  where meta_key='_stock' )\n" +
                "as stock_table\n" +
                "WHERE p.id=sku_table.pId and p.id=stock_table.pId and pm.post_id = sku_table.pId and pm.meta_key = '_price' ORDER BY stock;";
        try {
            rs = command.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rs;

    }


    private boolean sqlUpdShortDesc(String id, String shortDescription){

        String sqlUpdate = "UPDATE wp_posts\n" +
                "SET post_excerpt = ?\n" +
                "WHERE ID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlUpdate);
            statement.setString(1,shortDescription);
            statement.setString(2, id);
            statement.executeUpdate();
            connection.commit();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
