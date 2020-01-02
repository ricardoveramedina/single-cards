package com.pueblolavanda.singlecards.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Face;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchCSVLoaderInteractorTest {

    private static final String EXPANSION_CODE_XLN = "xln";
    private static final String EXPANSION_NAME_XLN = "Ixalan";
    private static final String NAME_XLN_1 = "Adanto Vanguard";
    private static final String NAME_XLN_1_ES = "Vanguardia de Adanto";
    private static final String LANG = "es";

    private static final String EXPANSION_CODE_UMA = "uma";
    private static final String NAME_UMA_1 = "Fire // Ice";
    public static final String EN = "en";

    private FetchExpansionInteractor expansionInteractor;
    private FetchCSVLoaderInteractor csvLoaderInteractor;

    @Mock
    private ApiScryfall apiScryfall;
    private ExpansionResponseModel expansionModel;
    private CSVLoaderResponseModel csvLoaderModel;


    @Before
    public void setUp() {
        this.expansionInteractor = new FetchExpansionInteractor(apiScryfall);
        this.csvLoaderInteractor = new FetchCSVLoaderInteractor();
    }

    @Test
    public void check_csvloader_returns_etiquetas_inventario_valoraciones(){

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setName(EXPANSION_NAME_XLN);
        expansion.setCardCount(15);

        Card card = new Card();
        card.setName(NAME_XLN_1);
        card.setNameEs(NAME_XLN_1_ES);
        card.setLang(LANG);
        Map<String,Boolean> legalities = new HashMap<>();
        legalities.put("standard", true);
        legalities.put("legacy", true);
        legalities.put("modern", false);
        card.setLegalities(legalities);
        card.setRarity("rare");
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG);

        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        System.out.println(csvLoaderModel.getProducts().get(0).getEtiquetas());
        assertThat(csvLoaderModel.getProducts().get(0).getEtiquetas(), is(""));
        assertThat(csvLoaderModel.getProducts().get(0).getInventario(), is(0));
        assertThat(csvLoaderModel.getProducts().get(0).isPermitirValoraciones(), is(false));




    }

    @Test
    public void fetch_CSVLoader_returns_CSVLoaderResponseModel() {

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setCardCount(15);

        Card card = new Card();
        card.setName(NAME_XLN_1);
        card.setNameEs(NAME_XLN_1_ES);
        card.setLang(LANG);
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG);

        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        assertThat(expansionModel, isA(ExpansionResponseModel.class));
        assertThat(csvLoaderModel, isA(CSVLoaderResponseModel.class));
    }


    //TODO: test split
    @Ignore
    @Test
    public void CSVLoader_returns_CSVLoaderResponseModel_Product_Name_split(){

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_UMA);
        expansion.setCardCount(15);

        Card card = new Card();
        card.setName(NAME_UMA_1);
        card.setLang(EN);
        card.setLayout("split");
        card.setCollectorNumber("225");
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        expansion.setCards(cards);

        List<Face> faces = new ArrayList<>();
        Face face = new Face();
        face.setName("Fire");
        faces.add(face);

        face = new Face();
        face.setName("Ice");
        faces.add(face);



        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_UMA,EN);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        assertThat(expansionModel.getExpansion().getCards().get(0).getName(), is(NAME_UMA_1));
        assertThat(csvLoaderModel.getProducts().get(0).getNombre(), is (NAME_UMA_1));

    }

    @Ignore
    @Test
    public void fetch_name_transform_card_product(){

    }

    @Test
    public void fetch_CSVLoader_returns_CSVLoaderResponseModel_Product_Name(){

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setCardCount(15);

        Card card = new Card();
        card.setName(NAME_XLN_1);
        card.setNameEs(NAME_XLN_1_ES);
        card.setLang(LANG);
        card.setLayout("normal");
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        assertThat(expansionModel.getExpansion().getCards().get(0).getName(), is(NAME_XLN_1));
        assertThat(csvLoaderModel.getProducts().get(0).getNombre(), is (NAME_XLN_1 + " - " + NAME_XLN_1_ES));

    }

    @Test
    public void check_CSVLoader_return_imageURL(){

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setName(EXPANSION_NAME_XLN);
        expansion.setCardCount(15);

        Card card = new Card();
        card.setName(NAME_XLN_1);
        card.setNameEs(NAME_XLN_1_ES);
        card.setCollectorNumber("1");
        card.setLang(LANG);
        card.setLayout("normal");
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        assertThat(csvLoaderModel.getProducts().get(0).getImagenes(), is ("https://pueblolavanda.cl/wp-content/uploads/2019/02/ixalan_1_adanto_vanguard.jpg"));
    }


    @Test
    public void check_bigdecimal_round(){

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setName(EXPANSION_NAME_XLN);
        expansion.setCardCount(15);

        Card card = new Card();
        card.setName(NAME_XLN_1);
        card.setNameEs(NAME_XLN_1_ES);
        card.setCollectorNumber("1");
        card.setPriceCK(new BigDecimal("0.99"));
        card.setLang(LANG);
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);


        assertThat(csvLoaderModel.getProducts().get(0).getPrecioNormal(), is(new BigDecimal("990")));
    }


    //Integration test




    @Ignore
    @Test
    public void fetch_CSVLoader_returns_CSVLoaderResponseModel_to_json() throws JsonProcessingException {

        apiScryfall = new ApiScryfall();
        this.expansionInteractor = new FetchExpansionInteractor(apiScryfall);
        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        assertThat(expansionModel.getExpansion().getCards().get(0).getName(), is(NAME_XLN_1));
        assertThat(csvLoaderModel.getProducts().get(0).getNombre(), is (NAME_XLN_1));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(csvLoaderModel);
        System.out.println(json);
    }

    @Ignore
    @Test
    public void fetch_CSVLoader_returns_CSVLoader_description(){

        apiScryfall = new ApiScryfall();
        this.expansionInteractor = new FetchExpansionInteractor(apiScryfall);
        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        assertThat(csvLoaderModel.getProducts().get(0).getDescripcion(), is (NAME_XLN_1));
    }

}