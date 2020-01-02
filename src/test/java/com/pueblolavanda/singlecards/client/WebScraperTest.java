package com.pueblolavanda.singlecards.client;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class WebScraperTest {


    private static final boolean NOT_FOIL = false;
    private static final boolean IS_FOIL = true;
    private final String EXPANSION_NAME_XLN = "Ixalan";
    private final String EXPANSION_NAME_GRN = "Guilds of Ravnica";
    private final String CARD_NAME_XLN = "Legion's Landing // Adanto, the First Fort";
    private final String CARD_NAME_HALF_GRN = "Assure // Assemble";
    private final String CARD_CONDITION_NM = "NM";
    private final String CARD_CONDITION_G = "G";

    private final String EXPANSION_NAME_UMA = "Ultimate Masters";
    private final String CARD_NAME_UMA = "hero of iroas";


    private WebScraper webScraper;

    @Mock
    private WebScraper webScraperMock;


    //integration test




    @Test
    public void checkPrice() {
        webScraper = new WebScraper();
        webScraper.setExpansionIDForCK(EXPANSION_NAME_UMA);

        BigDecimal price = webScraper.fetchPriceCardSite(CARD_NAME_UMA, CARD_CONDITION_NM, NOT_FOIL);
        BigDecimal priceToTest = new BigDecimal("0.25");

        assertThat(price, is(priceToTest));

    }

    @Test
    public void testThreshold(){
        for (int i=0; i < 10; i++ ){
            checkPrice();
        }

    }


    @Ignore
    @Test
    public void check_if_cardCondition_G() {
        webScraper = new WebScraper();
        webScraper.setExpansionIDForCK(EXPANSION_NAME_XLN);
        BigDecimal price = webScraper.fetchPriceCardSite(CARD_NAME_XLN, CARD_CONDITION_G, NOT_FOIL);
        BigDecimal priceToTest = new BigDecimal("4.00");
        assertThat(price, is(priceToTest));
    }


    @Test
    public void check_if_cardCondition_VG() {
        webScraper = new WebScraper();
        webScraper.setExpansionIDForCK("Dark Ascension");
        BigDecimal price = webScraper.fetchPriceCardSite("Curse of Bloodletting", "VG", NOT_FOIL);
        BigDecimal priceToTest = new BigDecimal("0.90");
        assertThat(price, is(priceToTest));
    }

    @Ignore
    @Test
    public void check_the_comma_in_name() {
        webScraper = new WebScraper();
        webScraper.setExpansionIDForCK("Commander Anthology");
        BigDecimal price = webScraper.fetchPriceCardSite("borrowing 100,000 arrows", "NM", NOT_FOIL);
        BigDecimal priceToTest = new BigDecimal("0.25");
        assertThat(price, is(priceToTest));
    }


    @Test
    public void check_card_in_half() {
        webScraper = new WebScraper();
        webScraper.setExpansionIDForCK(EXPANSION_NAME_GRN);
        BigDecimal price = webScraper.fetchPriceCardSite(CARD_NAME_HALF_GRN, CARD_CONDITION_G, NOT_FOIL);
        BigDecimal priceToTest = new BigDecimal("0.12");
        assertThat(price, is(priceToTest));
    }

    @Test
    public void check_price_of_foil(){
        webScraper = new WebScraper();
        webScraper.setExpansionIDForCK(EXPANSION_NAME_XLN);
        BigDecimal price = webScraper.fetchPriceCardSite(CARD_NAME_XLN, CARD_CONDITION_NM, IS_FOIL);
        BigDecimal priceToTest = new BigDecimal("10.99");
        assertThat(price, is(priceToTest));
    }


    @Ignore
    @Test
    public void check_expansion_name_exist() {

    }


}