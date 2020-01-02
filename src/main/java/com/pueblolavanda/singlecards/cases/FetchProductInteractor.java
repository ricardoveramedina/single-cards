package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.drivers.db.ConnPL;
import com.pueblolavanda.singlecards.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class FetchProductInteractor {

    private ConnPL connPL;

    @Autowired
    public FetchProductInteractor(ConnPL connPL){this.connPL = connPL;}

    public List<ProductResponseModel> getCardsStockPL() {
        List<Product> products = this.connPL.currentCardInStock();
        List<ProductResponseModel> productResponseModelsList = new ArrayList<>();
        for(Product product : products){
            productResponseModelsList.add(new ProductResponseModel(product));
        }
        return productResponseModelsList;
    }

    public List<ProductResponseModel> productsToUpdate(List<SingleResponseModel> singleResponseModelsFromScrap,
                                                       List<SingleResponseModel> singleResponseModelsFromPL){

        List<ProductResponseModel> productResponseModelFromScrap = new ArrayList<>();
        List<ProductResponseModel> productResponseModelFromPL = new ArrayList<>();

        for(int i=0; i < singleResponseModelsFromScrap.size(); i++) {
            productResponseModelFromScrap.add(singleToProduct(singleResponseModelsFromScrap.get(i)));
            productResponseModelFromPL.add(singleToProduct(singleResponseModelsFromPL.get(i)));
        }

        return productsToUpdateByPrice(productResponseModelFromScrap,productResponseModelFromPL);
    }


    private List<ProductResponseModel> productsToUpdateByPrice(List<ProductResponseModel> productResponseModelFromScrap,
                                                               List<ProductResponseModel> productResponseModelFromPL){

        List<ProductResponseModel> productResponseModelsToUpdate = new ArrayList<>();

        for(int i=0; i < productResponseModelFromPL.size(); i++) {
            if (!productResponseModelFromScrap.get(i).getProduct().getPrecioNormal()
                    .equals(productResponseModelFromPL.get(i).getProduct().getPrecioNormal())) {
                productResponseModelsToUpdate.add(productResponseModelFromScrap.get(i));
            }
        }


        return productResponseModelsToUpdate;
    }


    public List<ProductResponseModel> checkEmptyPrice(List<ProductResponseModel> productResponseModelListUpdate){

        List<ProductResponseModel> productsHasError = new ArrayList<>();

        for(ProductResponseModel productResponseModel : productResponseModelListUpdate){
            if(productResponseModel.getProduct().getPrecioNormal().equals(new BigDecimal("0"))){
                productsHasError.add(productResponseModel);
            }
        }

        return productsHasError;
    }

    public List<ProductResponseModel> removeEmptyPrice(List<ProductResponseModel> productResponseModelListUpdate) {
        productResponseModelListUpdate.removeIf(productResponseModel ->
                productResponseModel.getProduct().getPrecioNormal().equals(new BigDecimal("0"))
        );
        return productResponseModelListUpdate;
    }



    private ProductResponseModel singleToProduct(SingleResponseModel singleResponseModel){

        Product product = new Product();

        BigDecimal price = singleResponseModel.getSingle().getPrice();
        int isFoil;

        isFoil = ((singleResponseModel.getSingle().isFoil()) ? 1 : 0);

        //M-1-NM-xln-0-ES
        //TODO: muy raro el transform desde el domain "single"
        String conditionCode = singleResponseModel.getSingle().getCondition();
        String sku = "M-" + singleResponseModel.getSingle().getCollectorNumber() + "-" +
                singleResponseModel.getSingle().conditionTransform(conditionCode) + "-" +
                singleResponseModel.getSingle().getExpansionCode() + "-" +
                isFoil + "-" +
                singleResponseModel.getSingle().getLang();

        product.setSku(sku);
        product.setPrecioNormal(price);
        product.setNombre(singleResponseModel.getSingle().getName());

        return new ProductResponseModel(product);
    }

}
