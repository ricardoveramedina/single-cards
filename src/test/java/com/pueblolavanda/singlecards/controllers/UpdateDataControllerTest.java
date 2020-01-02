package com.pueblolavanda.singlecards.controllers;

import com.pueblolavanda.singlecards.cases.CSVLoaderResponseModel;
import com.pueblolavanda.singlecards.cases.ExpansionResponseModel;
import com.pueblolavanda.singlecards.cases.FetchCSVLoaderInteractor;
import com.pueblolavanda.singlecards.cases.FetchExpansionInteractor;
import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)

public class UpdateDataControllerTest {

    public static final String EXPANSION_CODE_UMA = "uma";
    public static final String CARD_UMA_NAME = "Dark Depths";
    public static final String CARD_UMA_NAME_ES = "Abismos oscuros";
    public static final String LANG = "es";

    private FetchExpansionInteractor interactor;
    private FetchCSVLoaderInteractor csvLoaderInteractor;


    @Mock
    private ApiScryfall apiScryfall;


    @Before
    public void setUp() {
        this.interactor = new FetchExpansionInteractor(apiScryfall);
        this.csvLoaderInteractor = new FetchCSVLoaderInteractor();
    }

    //Integration test

    //TODO: PLD, HP
    @Test
    public void updateShortDescriptionTest(){
        UpdateDataController updateDataController = new UpdateDataController();
        updateDataController.updateShortDescription("EN", "NM");
    }



    @Test
    public void insertSeoTest() {


        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_UMA);
        expansion.setName("Ultimate Masters");
        expansion.setCardCount(15);

        List<Product> productList = new ArrayList<>();
        Product product = new Product();

        product.setSku("M-2-NM-uma-0-en");
        product.setNombre("Artisan of Kozilek - Artesano de Kozilek");
        product.setDescripcionCorta("Cartas Sueltas Magic: Artisan of Kozilek - Artesano de Kozilek - Ultimate Masters - uncommon");

        productList.add(product);

        CSVLoaderResponseModel csvLoaderResponseForOne = new CSVLoaderResponseModel(productList);

        UpdateDataController updateDataController = new UpdateDataController();
        updateDataController.insertSeo(csvLoaderResponseForOne);


    }
}