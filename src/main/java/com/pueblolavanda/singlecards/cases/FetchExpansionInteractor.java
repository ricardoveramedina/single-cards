package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.client.WebScraper;
import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FetchExpansionInteractor {

    private ApiScryfall apiScryfall;
    private WebScraper webScraper;
    private final String CARD_CONDITION_NM = "NM";

    @Autowired
    public FetchExpansionInteractor(ApiScryfall apiScryfall) {
        this.apiScryfall = apiScryfall;
    }

    public ExpansionResponseModel fetch(String expansionCode, String lang) {
        Expansion expansion = this.apiScryfall.fetchExpansion(expansionCode, lang);

        return new ExpansionResponseModel(expansion);
    }

    public ExpansionResponseModel removeCard(Expansion expansion){
        expansion.getCards().removeIf(card -> card.isMeldResult());
        return new ExpansionResponseModel(expansion);
    }

    public ExpansionResponseModel setAsNotFoil(Expansion expansion){
        for(Card card : expansion.getCards()){
            card.setFoil(false);
        }
        return new ExpansionResponseModel(expansion);
    }

    public ExpansionResponseModel fetchCardSitePrice(Expansion expansion){
        webScraper = new WebScraper();
        webScraper.setExpansionIDForCK(expansion.getName());
        if(webScraper.expansionID_CK == 0){
            System.out.println("get the new code for " + expansion.getCode());
            webScraper.expansionID_CK = expansion.getExpansionID(expansion.getCode());
        }
        for(Card card : expansion.getCards()){
            BigDecimal priceCK = webScraper.fetchPriceCardSite(card.getName(),CARD_CONDITION_NM, card.isFoil());
            card.setPriceCK(priceCK);
        }
        return new ExpansionResponseModel(expansion);
    }

    public ExpansionResponseModel setImageCardInEnglish(Expansion expansion) {

        for(Card card : expansion.getCards()){
            Single single = this.apiScryfall.fetchSingle(expansion.getCode(),"en",card.getCollectorNumber());
            if(card.getLayout().contains("split") || card.getLayout().contains("transform")){
                for(int i=0; i < card.getFaces().size() ;i++) {
                    String imageSingle = single.getFaces().get(i).getImageUrl();
                    card.getFaces().get(i).setImageUrl(imageSingle);
                }
            }
            else
            {
                card.setImageUrl(single.getImageUrl());
            }
        }

        return new ExpansionResponseModel(expansion);
    }



}