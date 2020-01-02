package com.pueblolavanda.singlecards.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Single extends Card {
    private String condition;

    @JsonProperty("set")
    private String expansionCode;

    @JsonProperty("set_name")
    private String expansionName;

    private BigDecimal price;


    private enum ConditionCode {
        NM,EX,PLD,HP,VG,G
    }

    public String conditionTransform(String conditionCK) {
        ConditionCode condition = ConditionCode.valueOf(conditionCK.toUpperCase());
        switch (condition){
            case NM: return "NM";
            case EX: return "EX";
            case PLD: return "VG";
            case HP: return "G";
            case VG: return "PLD";
            case G: return "HP";
        }
        return "";
    }
}
