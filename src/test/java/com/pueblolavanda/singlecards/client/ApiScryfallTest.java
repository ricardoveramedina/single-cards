package com.pueblolavanda.singlecards.client;

import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Single;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ApiScryfallTest {

    public static final String XLN = "xln";
    public static final String DOM = "dom";
    public static final String V17 = "v17";
    public static final String G18 = "g18";
    public static final String LANG_ES = "es";
    public static final String LANG_EN = "en";
    public static final String CARD_NAME = "Terramorphic Expanse";

    private ApiScryfall apiScryfall;

    @Before
    public void setUp() {
        apiScryfall = new ApiScryfall();
    }


    //Integration test

    @Test
    public void fetchCardEsTest(){
        Card card = apiScryfall.fetchCard(CARD_NAME,LANG_ES);

        assertThat(card.getNameEs(),is("Expansión terramórfica"));
    }


    @Test
    public void fetchCardEsApostrofeTest(){
        Card card = apiScryfall.fetchCard("Akroma's Vengeance",LANG_ES);
        System.out.println(card.getOracleEs());

        assertThat(card.getNameEs(),is("Venganza de Akroma"));
    }

    @Test
    public void fetchCardOnlyEnTest(){
        //Card card = apiScryfall.fetchCard(CARD_NAME,LANG_ES);
        Card card = apiScryfall.fetchCard("leovold",LANG_ES);
        assertThat(card.getNameEs(),is("a"));
    }

    @Test
    public void fetchSingleTest(){
        Single single = apiScryfall.fetchSingle(XLN,LANG_ES,"1");
        assertThat(single.getExpansionCode(), is(XLN));
        assertThat(single.getExpansionName(), is("Ixalan"));
    }



    @Test
    public void fetchExpansion() {
        Expansion expansion = apiScryfall.fetchExpansion(G18,LANG_EN);
        assertThat(expansion.getCode(), is(G18));
    }

    @Test
    public void fetchExpansionName() {
        Expansion expansion = apiScryfall.fetchExpansion(DOM,LANG_ES);
        assertThat(expansion.getName(),is("Dominaria"));
    }


    @Test
    public void fetch_number_of_Cards_in_Expansion(){
        Expansion expansion = apiScryfall.fetchExpansion(G18,LANG_EN);
        assertThat(expansion.getCardCount(), is(5));
    }

    @Test
    public void fetchCardName(){
        Expansion expansion = apiScryfall.fetchExpansion(XLN,LANG_ES);
        assertThat(expansion.getCards().get(0).getName(), is("Akroma's Vengeance"));
    }

    @Test
    public void fetchCardImageUrl(){
        Expansion expansion = apiScryfall.fetchExpansion(XLN,LANG_ES);
        assertThat(expansion.getCards().get(0).getImageUrl(), is("https://img.scryfall.com/cards/normal/es/xln/1.jpg?1527385219"));
    }

    @Test
    public void fetch_name_of_two_face_card(){
        Expansion expansion = apiScryfall.fetchExpansion(V17,LANG_EN);
        assertThat(expansion.getCards().get(0).getFaces().get(0).getName(), is("Archangel Avacyn"));
    }

    @Test
    public void fetch_type_line(){
        Expansion expansion = apiScryfall.fetchExpansion(XLN,LANG_ES);
        assertThat(expansion.getCards().get(0).getTypeLine(), is ("Creature — Vampire Soldier"));
    }

    @Test
    public void fetch_the_back_image_of_two_face_Card(){
        Expansion expansion = apiScryfall.fetchExpansion(V17,LANG_EN);
        assertThat(expansion.getCards().get(0).getFaces().get(1).getImageUrl(), is("https://img.scryfall.com/cards/normal/en/v17/1b.jpg?1519598244"));
    }

}