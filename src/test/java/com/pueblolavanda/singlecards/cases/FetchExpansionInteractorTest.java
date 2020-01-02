package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.domain.Expansion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchExpansionInteractorTest {

    public static final String EXPANSION_CODE_XLN = "xln";
    public static final int XLN_CARD_COUNT = 289;
    public static final String NAME_ES_XLN = "Vanguardia de Adanto";
    public static final String TYPE_LINE_ES_XLN = "Criatura — Soldado vampiro";
    public static final String LANG_ES = "es";
    public static final String LANG_EN = "en";

    public static final String EXPANSION_CODE_HOP = "hop";
    public static final int HOP_CARD_COUNT = 20;


    public static final String EXPANSION_CODE_V17 = "v17";
    public static final int V17_CARD_COUNT = 16;

    private FetchExpansionInteractor interactor;

    @Mock
    private ApiScryfall apiScryfall;

    @Before
    public void setUp() {
        this.interactor = new FetchExpansionInteractor(apiScryfall);
    }

    @Test
    public void fetch_expansion_returns_ExpansionResponseModel() {
        ExpansionResponseModel model = interactor.fetch(EXPANSION_CODE_XLN,LANG_ES);
        assertThat(model, isA(ExpansionResponseModel.class));
    }


    @Test
    public void fetch_expansion_response_has_expansion_code() {
        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setCardCount(289);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        ExpansionResponseModel expansionResponseModel = interactor.fetch(EXPANSION_CODE_XLN,LANG_ES);


        assertThat(expansionResponseModel.getExpansion().getCode(), is(EXPANSION_CODE_XLN));
        assertThat(expansionResponseModel.getExpansion().getCardCount(), is(XLN_CARD_COUNT));
    }

    @Test
    public void fetch_expansion_response_has_expansion_code_accordingly() {
        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_HOP);
        expansion.setCardCount(20);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        ExpansionResponseModel expansionResponseModel = interactor.fetch(EXPANSION_CODE_HOP,LANG_ES);

        assertThat(expansionResponseModel.getExpansion().getCode(), is(EXPANSION_CODE_HOP));
        assertThat(expansionResponseModel.getExpansion().getCardCount(), is(HOP_CARD_COUNT));
    }

    //integration test

    @Test
    public void check_legality_card(){

        apiScryfall = new ApiScryfall();
        interactor = new FetchExpansionInteractor(apiScryfall);
        ExpansionResponseModel expansionResponseModel = interactor.fetch(EXPANSION_CODE_V17,LANG_EN);
        assertThat(expansionResponseModel.getExpansion().getCards().size(), is(V17_CARD_COUNT));

        assertThat(expansionResponseModel.getExpansion().getCards().get(0).getLegalities().get("legacy"), is(true));
    }

    @Test
    public void check_card_count_V17(){
        apiScryfall = new ApiScryfall();
        interactor = new FetchExpansionInteractor(apiScryfall);
        ExpansionResponseModel expansionResponseModel = interactor.fetch(EXPANSION_CODE_V17,LANG_EN);
        assertThat(expansionResponseModel.getExpansion().getCards().size(), is(V17_CARD_COUNT));
    }

    @Test
    public void check_card_lang(){
        apiScryfall = new ApiScryfall();
        interactor = new FetchExpansionInteractor(apiScryfall);
        ExpansionResponseModel expansionResponseModel = interactor.fetch(EXPANSION_CODE_XLN,LANG_ES);
        assertThat(expansionResponseModel.getExpansion().getCards().get(0).getNameEs(), is(NAME_ES_XLN));
        assertThat(expansionResponseModel.getExpansion().getCards().get(0).getTypeLineEs(), is(TYPE_LINE_ES_XLN));
    }

}