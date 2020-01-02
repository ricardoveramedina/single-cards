package com.pueblolavanda.singlecards.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class Face {

    private String name;

    @JsonProperty("printed_name")
    private String nameEs = "";

    private String artist = "";

    @JsonProperty("type_line")
    private String typeLine = "";

    @JsonProperty("printed_type_line")
    private String typeLineEs = "";

    @JsonProperty("oracle_text")
    private String oracleText = "";

    @JsonProperty("printed_text")
    private String oracleEs = "";

    @JsonProperty("Flavor_text")
    private String flavorText = "";

    private String imageUrl = "";

    @JsonProperty("image_uris")
    private void unpackNestedImage(Map<String,Object> images) {
        this.imageUrl = (String)images.get("normal");
    }

    @JsonProperty("mana_cost")
    private String manaCost = "";

}
