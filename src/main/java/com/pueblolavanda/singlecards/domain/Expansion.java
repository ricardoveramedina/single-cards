package com.pueblolavanda.singlecards.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Expansion {

    private String name = "";
    private String code = "";


    @JsonProperty("has_more")
    private boolean hasMore = false;

    @JsonProperty("card_count")
    private int cardCount = 0;

    @JsonProperty("data")
    private List<Card> cards = new ArrayList<>();



    private enum ExpansionCode {
        M11, M12, M14, CM2
    }

    public int getExpansionID(String code) {
        ExpansionCode expCode = ExpansionCode.valueOf(code.toUpperCase());
        switch (expCode){
            case M11: return 2847;
            case M12: return 2863;
            case M14: return 2895;
            case CM2: return 3089;
        }
        return 0;
    }
}
