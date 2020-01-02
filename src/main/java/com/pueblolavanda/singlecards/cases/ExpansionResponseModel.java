package com.pueblolavanda.singlecards.cases;
import com.pueblolavanda.singlecards.domain.Expansion;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class ExpansionResponseModel {
    private Expansion expansion;
}
