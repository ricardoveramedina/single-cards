package com.pueblolavanda.singlecards.client;

import com.jsunsoft.http.HttpMethod;
import com.jsunsoft.http.HttpRequest;
import com.jsunsoft.http.HttpRequestBuilder;
import com.jsunsoft.http.ResponseDeserializer;
import com.pueblolavanda.singlecards.domain.Expansion;

import java.math.BigDecimal;
import java.net.URLEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static com.pueblolavanda.singlecards.StringHelper.cleanFilenameURL;

public class WebScraper {
    private static final String PATTERN_CATEGORIES_CK = "<select name=\"filter\\[category_id\\]\">(.+?)<\\/select>";
    private static final String PATTERN_CATEGORIE_ID_1_CK = ".*<option value=\"(.*)\">";
    private static final String PATTERN_CATEGORIE_ID_2_CK = "<\\/option>";
    private static final String URL_ADVANCE_SEARCH_CK = "";

    private static final String firstPricePatternRegex = "value=\"";
    private static final String secondPricePatternRegex = "\">.*?\"stylePrice\"> \\$(.+?) <\\/span>";
    private static final String patternCleanNameRegExp = "(.+?) //.*";

    private static final String advanceSearchUrlCK = "";
    private static final String advanceSearchFinalUrlCK = "&filter%5Bcategory_id%5D=";
    private static final String advanceSearchFilterFoil = "&filter%5Btab%5D=mtg_foil";
    private int waitTimeSeconds = 900;

    public int expansionID_CK;

    public WebScraper() {

    }


    public BigDecimal fetchPriceCardSite(String cardName, String cardCondition, boolean isFoil){


        try {
            TimeUnit.MILLISECONDS.sleep(waitTimeSeconds);
            waitTimeSeconds = 900;
        } catch (Exception e) {
        }

        //Get the name of the front card
        if(cardName.contains("//")){cardName = askRegexp(cardName,patternCleanNameRegExp);}
        cardName = cleanFilenameURL(cardName);

        String body = "";

        String url = advanceSearchUrlCK + URLEncoder.encode(cardName)
                + advanceSearchFinalUrlCK + expansionID_CK;
        if(isFoil) url += advanceSearchFilterFoil;

        String patternRegExp =  firstPricePatternRegex + cardCondition + secondPricePatternRegex;

        boolean ddosFlag = true;

        while(ddosFlag){
            try {
                body = new ConnHTTP(url).getContent();
                ddosFlag = false;
                return new BigDecimal(askRegexp(body, patternRegExp));
            }
            catch (Exception e){
                System.out.println("Error:" + e);
                System.out.println(cardName + "-" + cardCondition + "-" + isFoil + "-" +  url);
                ddosFlag=true;
                waitTimeSeconds = 6000;
            }
        }

        return new BigDecimal("0");
    }

    public void setExpansionIDForCK(String expansionName) {
        String body = "";
        try {
            body = new ConnHTTP(URL_ADVANCE_SEARCH_CK).getContent();
            waitTimeSeconds = 1;
        }
        catch(com.jsunsoft.http.NoSuchContentException error){
            waitTimeSeconds = 6;
        }
        body = askRegexp(body,PATTERN_CATEGORIES_CK);
        String patternRegExp =  PATTERN_CATEGORIE_ID_1_CK + expansionName + PATTERN_CATEGORIE_ID_2_CK;
        String result = askRegexp(body,patternRegExp);

        try {
            expansionID_CK = Integer.parseInt(result);
        }
        catch(java.lang.NumberFormatException error){
            System.out.println("code not found, set to 0 : " + error);
            expansionID_CK = 0;
        }

    }



    private String askRegexp(String body, String pattern){

        Pattern regExp = Pattern.compile(pattern);
        body = body.replaceAll("\n"," ");
        Matcher m = regExp.matcher(body);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private class ConnHTTP{

        private HttpRequest<String> httpRequest;
        private ConnHTTP(String url){
            httpRequest = HttpRequestBuilder.create(HttpMethod.GET, url, String.class )
                    .trustAllCertificates()
                    .trustAllHosts()
                    .addDefaultHeader(ACCEPT, APPLICATION_JSON.getMimeType())
                    .enableDefaultRedirectStrategy()
                    .responseDeserializer(ResponseDeserializer.toStringDeserializer())
                    .build();
        }
        private String getContent() {
            return httpRequest.execute().get();
        }

    }
}
