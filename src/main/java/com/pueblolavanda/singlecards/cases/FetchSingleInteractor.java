package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.client.WebScraper;
import com.pueblolavanda.singlecards.domain.Product;
import com.pueblolavanda.singlecards.domain.Single;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pueblolavanda.singlecards.cases.Pricing.*;

public class FetchSingleInteractor {

    private static final String PATTERN_SKU = "M-(.+)-(.+)-(.+)-(.+)-(.+)";

    private ApiScryfall apiScryfall;


    @Autowired
    public FetchSingleInteractor(ApiScryfall apiScryfall) {
        this.apiScryfall = apiScryfall;
    }

    public SingleResponseModel fetch(ProductResponseModel productResponseModel) {

        SingleResponseModel singleResponseModel = productToSingle(productResponseModel);

        String expansionCode = singleResponseModel.getSingle().getExpansionCode();
        String language = singleResponseModel.getSingle().getLang();
        String cardNumber = singleResponseModel.getSingle().getCollectorNumber();
        BigDecimal price = singleResponseModel.getSingle().getPrice();
        String condition = singleResponseModel.getSingle().getCondition();
        boolean isFoil = singleResponseModel.getSingle().isFoil();

        Single single = this.apiScryfall.fetchSingle(expansionCode, language, cardNumber);
        single.setPrice(price);
        single.setCondition(condition);
        single.setFoil(isFoil);
        single.setLang(language);

        return new SingleResponseModel(single);
    }


    public SingleResponseModel setPriceFromCardSite(Single single, WebScraper webScraper){


        BigDecimal priceCK = webScraper.fetchPriceCardSite(single.getName(),single.getCondition(),single.isFoil());
        BigDecimal actualPrice = convertPriceToPL(priceCK,single.getRarity(),single.getCondition());

        single.setPrice(actualPrice);

        return new SingleResponseModel(single);
    }


    private SingleResponseModel productToSingle(ProductResponseModel productResponseModel){

        Product product = productResponseModel.getProduct();
        String sku = product.getSku();
        Single single = askRegexpSKUData(sku,PATTERN_SKU);
        single.setPrice(product.getPrecioNormal());

        return new SingleResponseModel(single);
    }


    private Single askRegexpSKUData(String body, String pattern){

        Pattern regExp = Pattern.compile(pattern);
        body = body.replaceAll("\n"," ");
        Matcher m = regExp.matcher(body);
        Single single = new Single();
        while (m.find()) {
             single.setCollectorNumber(m.group(1));
             single.setCondition(single.conditionTransform(m.group(2)));
             single.setExpansionCode(m.group(3));
             if(m.group(4).contentEquals("1")){single.setFoil(true);}
             else{
                 single.setFoil(false);
             }
             single.setLang(m.group(5));
        }
        return single;
    }

}
