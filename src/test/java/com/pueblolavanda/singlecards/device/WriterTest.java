package com.pueblolavanda.singlecards.device;

import com.pueblolavanda.singlecards.cases.CSVLoaderResponseModel;
import com.pueblolavanda.singlecards.cases.ExpansionResponseModel;
import com.pueblolavanda.singlecards.cases.FetchCSVLoaderInteractor;
import com.pueblolavanda.singlecards.cases.FetchExpansionInteractor;
import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Face;
import com.pueblolavanda.singlecards.drivers.device.Writer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WriterTest {

    private static final String EXPANSION_CODE_XLN = "xln";
    private static final String EXPANSION_NAME_XLN = "Ixalan";
    private static final String NAME_XLN_1 = "Adanto Vanguard";
    private static final String NAME_XLN_1_ES = "Vanguardia de Adanto";

    private static final String NAME_XLN_1_FACE = "Arguel's Blood Fast // Temple of Aclazotz";
    private static final String NAME_XLN_1_FACE_1 = "Arguel's Blood Fast";
    private static final String NAME_XLN_1_FACE_2 = "Temple of Aclazotz";
    private static final String NAME_XLN_1_FACE_1_ES = "Ayuno de Sangre de Arguel";
    private static final String NAME_XLN_1_FACE_2_ES = "Templo de Aclazotz";

    private static final String LANG_ES = "es";


    private static final String NAME_XLN_2 = "Bla bla";
    private static final String IMAGE_URL_FACE_1 = "https://img.scryfall.com/cards/normal/en/xln/90a.jpg?1527428806";
    private static final String IMAGE_URL_FACE_2 = "https://img.scryfall.com/cards/normal/en/xln/90b.jpg?1527428806";
    private static final String PATH = "/Users/kojinanjo/Documents/single-cards/";

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
    public void csvWriter() throws IOException {

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setName(EXPANSION_NAME_XLN);

        List<Card> cards = new ArrayList<>();

        Card card = new Card();
        card.setName(NAME_XLN_1);
        card.setNameEs(NAME_XLN_1_ES);
        card.setCollectorNumber("1");
        card.setLang(LANG_ES);
        card.setLayout("normal");
        cards.add(card);

        card = new Card();
        card.setName(NAME_XLN_2);
        card.setCollectorNumber("2");
        card.setLang(LANG_ES);
        cards.add(card);

        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG_ES);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        Writer writer = new Writer();
        writer.csvWriter(csvLoaderModel,EXPANSION_NAME_XLN);

        assertThat(csvLoaderModel.getProducts().get(0).getNombre(), is(NAME_XLN_1 + " - " + NAME_XLN_1_ES));

    }

    @Test
    public void csvWriter_check_face_name() throws IOException {

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setName(EXPANSION_NAME_XLN);

        List<Card> cards = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        Card card = new Card();
        card.setName(NAME_XLN_1_FACE);
        card.setNameEs("");
        card.setLayout("transform");
        card.setLang(LANG_ES);

        card.setCollectorNumber("1");

        Face face = new Face();
        face.setNameEs(NAME_XLN_1_FACE_1_ES);
        face.setName(NAME_XLN_1_FACE_1);
        faces.add(face);

        face = new Face();
        face.setNameEs(NAME_XLN_1_FACE_2_ES);
        face.setName(NAME_XLN_1_FACE_2);
        faces.add(face);

        card.setFaces(faces);
        cards.add(card);
        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG_ES);
        csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

        Writer writer = new Writer();
        writer.csvWriter(csvLoaderModel,EXPANSION_NAME_XLN);

        assertThat(csvLoaderModel.getProducts().get(0).getNombre(), is(NAME_XLN_1_FACE + " - " + NAME_XLN_1_FACE_1_ES + " // " + NAME_XLN_1_FACE_2_ES));
    }

    @Test
    public void downloadImage_transform() throws IOException {

        Expansion expansion = new Expansion();
        expansion.setCode(EXPANSION_CODE_XLN);
        expansion.setName(EXPANSION_NAME_XLN);

        List<Card> cards = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        Card card = new Card();
        card.setName(NAME_XLN_1_FACE);
        card.setNameEs(NAME_XLN_1_FACE_1_ES);
        card.setLang(LANG_ES);
        card.setLayout("transform");

        card.setCollectorNumber("1");

        Face face = new Face();
        face.setImageUrl(IMAGE_URL_FACE_1);
        face.setName(NAME_XLN_1_FACE_1);
        faces.add(face);
        face = new Face();
        face.setName(NAME_XLN_1_FACE_2);
        face.setImageUrl(IMAGE_URL_FACE_2);
        faces.add(face);

        card.setFaces(faces);
        cards.add(card);
        expansion.setCards(cards);

        when(apiScryfall.fetchExpansion(anyString(),anyString())).thenReturn(expansion);

        expansionModel = expansionInteractor.fetch(EXPANSION_CODE_XLN,LANG_ES);

        Writer writer = new Writer();
        writer.downloadImage(PATH, expansionModel);

        assertThat(expansionModel.getExpansion().getCards().get(0).getFaces().get(0).getImageUrl(), is(IMAGE_URL_FACE_1));
    }


}