package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.domain.Product;
import lombok.Getter;
import lombok.Value;


@Getter
@Value
public class ProductResponseModel {
    private Product product;
}
