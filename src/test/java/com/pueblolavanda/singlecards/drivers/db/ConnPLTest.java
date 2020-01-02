package com.pueblolavanda.singlecards.drivers.db;

import com.pueblolavanda.singlecards.cases.FetchProductInteractor;
import com.pueblolavanda.singlecards.cases.ProductResponseModel;
import com.pueblolavanda.singlecards.domain.Product;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ConnPLTest {


    private static final String SKU = "M-1-NM-xln-0-es";
    private static final String PRICE = "990";
    public static final String XLN = "xln";

    private FetchProductInteractor productsInteractor;



    @Before
    public void setUp(){

    }


    //integration test

    @Ignore
    @Test
    public void checkSKUQueryMagicCardTest(){

        ConnPL connPL = new ConnPL();
        List<Product> productList = connPL.currentCardInStock();
        assertThat(productList.get(0).getSku(),is(SKU));

    }

    @Ignore
    @Test
    public void checkPriceQueryMagicCardTest(){

        ConnPL connPL = new ConnPL();
        List<Product> productList = connPL.currentCardInStock();
        assertThat(productList.get(0).getPrecioNormal(),is(new BigDecimal(PRICE)));

    }

    @Ignore
    @Test
    public void checkConnectionTest(){
        ConnPL connPL = new ConnPL();
        connPL.checkConnection();
    }


    @Ignore
    @Test
    public void updateMagicCardPriceTest(){
        ConnPL connPL = new ConnPL();

        Product product = new Product();
        product.setSku(SKU);
        product.setPrecioNormal(new BigDecimal("1990"));

        this.productsInteractor = new FetchProductInteractor(connPL);
        ProductResponseModel productResponseModel = new ProductResponseModel(product);

        connPL.updateMagicCardPrice(productResponseModel);

    }

}