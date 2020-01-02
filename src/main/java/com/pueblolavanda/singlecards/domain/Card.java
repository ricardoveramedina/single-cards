package com.pueblolavanda.singlecards.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;


@EqualsAndHashCode
@Setter @Getter
public class Card {

    @JsonProperty("collector_number")
    private String collectorNumber = "";

    private String name = "";

    @JsonProperty("printed_name")
    private String nameEs = "";

    private String rarity = "";
    private boolean foil = false;
    private BigDecimal priceCK = new BigDecimal(0);
    private String lang = "";
    private String layout = "";

    @JsonProperty("oracle_text")
    private String oracle = "";

    @JsonProperty("printed_text")
    private String oracleEs = "";

    @JsonProperty("type_line")
    private String typeLine = "";

    @JsonProperty("printed_type_line")
    private String typeLineEs = "";

    @JsonProperty("artist")
    private String artistName = "";

    @JsonProperty("mana_cost")
    private String manaCost = "";

    @JsonProperty("usd")
    private BigDecimal priceScry = new BigDecimal(0);

    @JsonProperty("flavor_text")
    private String flavorText = "";

    private String imageUrl;
    @JsonProperty("image_uris")
    private void unpackNestedImage(Map<String,Object> images) {
        imageUrl = (String)images.get("normal");
    }

    @JsonProperty("card_faces")
    private List<Face> faces = new ArrayList<>();

    private Map<String, Boolean> legalities = new HashMap<>();
    @JsonProperty("legalities")
    private void unpackNestedLegalities(Map<String,String> formats)
    {
        Set<String> keys = formats.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = formats.get(key);
            boolean isLegal = (value.equals("legal")) ? true : false;
            legalities.put(key,isLegal);
        }
    }

    private boolean isMeldResult;
    @JsonProperty("all_parts")
    private void unpackNestedPart(List<Map<String, Object>> parts) {
        for (int i = 0; i < parts.size(); i++)
        {
            if ( this.getName().contains((String)parts.get(i).get("name")) && (Boolean)((String) parts.get(i).get("component")).contains("meld_result") ){
                isMeldResult = true;
            }
        }
    }

}
