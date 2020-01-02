package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.domain.Product;
import com.pueblolavanda.singlecards.domain.Single;
import com.pueblolavanda.singlecards.drivers.db.ConnPL;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FetchProductInteractorTest {

    public static final String XLN = "xln";
    private FetchProductInteractor productInteractor;
    private List<ProductResponseModel> productResponseModelList;

    @Mock
    private ConnPL connPL;

    @Before
    public void setUp(){
        this.productInteractor = new FetchProductInteractor(connPL);
    }

    @Test
    public void check_productResponseModel_class() {
        productResponseModelList = productInteractor.getCardsStockPL();

        for(ProductResponseModel productResponseModel : productResponseModelList){
            assertThat(productResponseModel, isA(ProductResponseModel.class));
        }


    }

    @Test
    public void productsToUpdateByPriceTest(){
        //TODO: test
    }

    @Test
    public void singleToProductTest(){
        //TODO: test


        Single single = new Single();
        single.setPrice(new BigDecimal("990"));
        single.setExpansionCode(XLN);
        single.setCollectorNumber("1");
        single.setCondition("NM");
        single.setLang("es");
        single.setName("bla");
        single.setFoil(false);

        List<SingleResponseModel> singleResponseModelScrapList = new ArrayList<>();
        singleResponseModelScrapList.add(new SingleResponseModel(single));


        single = new Single();
        single.setPrice(new BigDecimal("1000"));
        single.setExpansionCode(XLN);
        single.setCollectorNumber("1");
        single.setCondition("NM");
        single.setLang("es");
        single.setName("bla");
        single.setFoil(false);

        List<SingleResponseModel> singleResponseModelPLList = new ArrayList<>();
        singleResponseModelPLList.add(new SingleResponseModel(single));

        List<ProductResponseModel> productResponseModelList = productInteractor.productsToUpdate(
                singleResponseModelScrapList, singleResponseModelPLList);

        assertThat(productResponseModelList.get(0).getProduct().getSku(),is("M-1-NM-xln-0-es"));
        assertThat(productResponseModelList.get(0).getProduct().getPrecioNormal(),is(new BigDecimal("990")));
        assertThat(productResponseModelList.get(0).getProduct().getNombre(),is("bla"));

    }

    @Test
    public void getCardsStockTest() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setPrecioNormal(new BigDecimal("2.0"));
        products.add(product);

        when(connPL.currentCardInStock()).thenReturn(products);

        productResponseModelList = productInteractor.getCardsStockPL();

        assertThat(productResponseModelList.get(0).getProduct().getPrecioNormal(), is(new BigDecimal("2.0")));
    }

    //Integration test

    @Test
    public void getCardsStockInPLTest() {

        connPL = new ConnPL();
        this.productInteractor = new FetchProductInteractor(connPL);
        productResponseModelList = productInteractor.getCardsStockPL();

        assertThat(productResponseModelList.get(0).getProduct().getPrecioNormal(), is(new BigDecimal("2989")));
    }



}