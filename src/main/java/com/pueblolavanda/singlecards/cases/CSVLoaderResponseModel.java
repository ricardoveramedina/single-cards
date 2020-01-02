package com.pueblolavanda.singlecards.cases;
import com.pueblolavanda.singlecards.domain.Product;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Getter
@Value
public class CSVLoaderResponseModel {
    private List<Product> products;
}
