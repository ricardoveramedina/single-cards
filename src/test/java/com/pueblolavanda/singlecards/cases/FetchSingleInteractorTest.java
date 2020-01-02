package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.domain.Product;
import com.pueblolavanda.singlecards.domain.Single;
import com.pueblolavanda.singlecards.drivers.db.ConnPL;
import org.junit.Before;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchSingleInteractorTest {

    public static final String XLN = "XLN";
    public static final String ES = "es";
    public static final String NM = "NM";
    public static final String M_1_NM_XLN_0_ES = "M-1-NM-xln-0-es";
    private FetchSingleInteractor singleInteractor;
    private FetchProductInteractor productsInteractor;

    @Mock
    private ApiScryfall apiScryfall;
    @Mock
    private ConnPL connPL;


    @Before
    public void setUp(){
        this.singleInteractor = new FetchSingleInteractor(apiScryfall);
        this.productsInteractor = new FetchProductInteractor(connPL);
    }

    @Test
    public void check_cardResponseModel_class() {
        Product product = new Product();
        product.setSku(M_1_NM_XLN_0_ES);

        product.setPrecioNormal(new BigDecimal("990"));

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Single single = new Single();
        single.setPrice(new BigDecimal("990"));
        single.setExpansionCode(XLN);
        single.setCollectorNumber("1");
        single.setFoil(false);

        when(connPL.currentCardInStock()).thenReturn(productList);
        when(apiScryfall.fetchSingle(anyString(),anyString(),anyString())).thenReturn(single);

        List<ProductResponseModel> productResponseModelList = productsInteractor.getCardsStockPL();
        List<SingleResponseModel> singleResponseModelList = new ArrayList<>();

        for(ProductResponseModel productResponseModel : productResponseModelList) {
            singleResponseModelList.add(singleInteractor.fetch(productResponseModel));
        }

        assertThat(singleResponseModelList.get(0), isA(SingleResponseModel.class));
    }


    @Test
    public void productToSingleTest(){
        Product product = new Product();
        product.setSku(M_1_NM_XLN_0_ES);

        product.setPrecioNormal(new BigDecimal("990"));

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Single single = new Single();
        single.setPrice(new BigDecimal("990"));
        single.setExpansionCode(XLN);
        single.setCollectorNumber("1");


        when(connPL.currentCardInStock()).thenReturn(productList);
        when(apiScryfall.fetchSingle(anyString(),anyString(),anyString())).thenReturn(single);

        List<ProductResponseModel> productResponseModelList = productsInteractor.getCardsStockPL();
        List<SingleResponseModel> singleResponseModelList = new ArrayList<>();
        for(ProductResponseModel productResponseModel : productResponseModelList) {
            singleResponseModelList.add(singleInteractor.fetch(productResponseModel));
        }


        assertThat(singleResponseModelList.get(0).getSingle().getExpansionCode().toUpperCase(),is(XLN));
        assertThat(singleResponseModelList.get(0).getSingle().getCondition(),is(NM));
        assertThat(singleResponseModelList.get(0).getSingle().getPrice(),is(new BigDecimal("990")));
        assertThat(singleResponseModelList.get(0).getSingle().getCollectorNumber(),is("1"));

    }
}