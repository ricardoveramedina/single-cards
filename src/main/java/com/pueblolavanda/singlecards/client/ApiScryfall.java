package com.pueblolavanda.singlecards.client;


import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Single;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import java.util.List;


@Component
public class ApiScryfall {

    private static final String BASE_URL = "https://api.scryfall.com/";
    private static final String CARDS_CALL = "cards/";
    private static final String LANG_CALL = "lang:";
    private static final String SEARCH_SET = "search?q=set:";
    private static final String SEARCH_CARD_NAME = "search?q=!\"";

    private static final String EXPANSION_CALL = "sets/";
    private static final String PAGE_CALL = "&page=";
    private static final String UNIQUE_CALL = "&unique=prints";
    private static final String ORDER_BY_CARD_NUMBER = "order:set";

    private int pageNumber = 1;
    private String lang = "en";
    private String URL = new String();

    public Expansion fetchExpansion(String expansionCode, String lang) {

        this.lang = lang;
        trustSelfSignedSSL();
        ApiConn apiConn = new ApiConn();
        Expansion expansion;
        if(this.lang.contains("en")){
            expansion = apiConn.expansionCall(expansionCode,"expansionData");
            expansion.setCards(apiConn.expansionCall(expansionCode,"cardList").getCards());
            mergeSpanishCards(expansion,"es");
        }
        else
        {
            expansion = mergeExpansionLang(expansionCode, apiConn);
        }

        return expansion;
    }


    private Expansion mergeSpanishCards(Expansion expansionEn,String lang){

        for(Card card : expansionEn.getCards()){
            Card cardEs = fetchCard(card.getName(),lang);
            if(cardEs.getName().isEmpty()) continue;

            card.setNameEs(cardEs.getNameEs());
            card.setOracleEs(cardEs.getOracleEs());
            card.setTypeLineEs(cardEs.getTypeLineEs());
            card.setFlavorText(cardEs.getFlavorText());

            if(card.getLayout().equals("split") || card.getLayout().equals("flip") ||
                    card.getLayout().equals("transform")){
                for(int i=0; i < card.getFaces().size();i++){
                    card.getFaces().get(i).setNameEs(cardEs.getFaces().get(i).getNameEs());
                    card.getFaces().get(i).setOracleEs(cardEs.getFaces().get(i).getOracleEs());
                    card.getFaces().get(i).setTypeLineEs(cardEs.getFaces().get(i).getTypeLineEs());
                }
            }
        }


        return expansionEn;
    }


    //If the expansion in other language is not the same as english, this code insert the omited cards
    private Expansion mergeExpansionLang(String expansionCode, ApiConn apiConn) {

        Expansion expansionLang = apiConn.expansionCall(expansionCode,"expansionData");
        expansionLang.setCards(apiConn.expansionCall(expansionCode,"cardList").getCards());


        this.lang = "en";
        Expansion expansionEN = apiConn.expansionCall(expansionCode,"expansionData");
        expansionEN.setCards(apiConn.expansionCall(expansionCode,"cardList").getCards());
        Expansion expansionTotal = expansionLang;
        if(expansionLang.getCards().size() != expansionEN.getCards().size()){

            List<Card> cardsEN = expansionEN.getCards();
            List<Card> cardsLang = expansionLang.getCards();

            boolean cardExist = false;

            for(Card cardEn : cardsEN) {
                for(Card cardLang :cardsLang){
                    if(cardEn.getCollectorNumber().contentEquals(cardLang.getCollectorNumber())){
                        cardExist = true;
                        break;
                    }
                    cardExist = false;
                }
                if(!cardExist){
                    expansionTotal.getCards().add(cardEn);
                }
            }
        }

        return expansionTotal;
    }


    public Single fetchSingle(String expansionCode, String lang, String cardNumber) {
        this.lang = lang;
        trustSelfSignedSSL();
        ApiConn apiConn = new ApiConn();

        return apiConn.singleCall(expansionCode,cardNumber);
    }

    //TODO: check 404 not found example leovold
    public Card fetchCard(String cardName, String lang) {
        this.lang = lang;
        trustSelfSignedSSL();
        ApiConn apiConn = new ApiConn();
        Card card = apiConn.cardNameSearch(cardName,lang);
        return card;
    }

    private class ApiConn {
        private RestTemplate restTemplate;

        private ApiConn() {
            this.restTemplate = new RestTemplate();
        }

        private Single singleCall(String expansionCode, String cardNumber){

            URL = BASE_URL + CARDS_CALL + expansionCode + "/" + cardNumber;
            ResponseEntity<Single> response = restTemplate.exchange(
                    URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Single>() {
                    });

            return response.getBody();
        }

        private Expansion expansionCall(String expansionCode, String callType) {

            if(callType.equalsIgnoreCase("expansionData")) {URL = BASE_URL + EXPANSION_CALL + expansionCode;}
            if(callType.equalsIgnoreCase("cardList")) {URL = BASE_URL + CARDS_CALL + SEARCH_SET + expansionCode + "+" +
                    LANG_CALL + lang + "+" + ORDER_BY_CARD_NUMBER + UNIQUE_CALL + PAGE_CALL + pageNumber;}

            ResponseEntity<Expansion> response = restTemplate.exchange(
                    URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Expansion>() {
                    });

            if(response.getBody().isHasMore()) {
                pageNumber ++;
                response.getBody().getCards().addAll(expansionCall(expansionCode,"cardList").getCards());
            }
            pageNumber = 1;
            return response.getBody();
        }

        private Card cardNameSearch(String cardName, String lang) {
            URL = BASE_URL + CARDS_CALL + SEARCH_CARD_NAME + cardName + "\"+" + LANG_CALL + lang;
            try {
                ResponseEntity<Expansion> response = restTemplate.exchange(
                        URL,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Expansion>() {
                        });
                return response.getBody().getCards().get(0);
            }
            catch(org.springframework.web.client.HttpClientErrorException ex)
            {
                ex.printStackTrace();
            }

            return new Card();
        }
    }


    public static void trustSelfSignedSSL() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLContext.setDefault(ctx);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
