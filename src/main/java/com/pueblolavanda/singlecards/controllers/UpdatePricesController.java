package com.pueblolavanda.singlecards.controllers;

import com.pueblolavanda.singlecards.cases.*;
import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.client.WebScraper;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Product;
import com.pueblolavanda.singlecards.domain.Single;
import com.pueblolavanda.singlecards.drivers.db.ConnPL;
import com.pueblolavanda.singlecards.drivers.device.Writer;

import java.util.ArrayList;
import java.util.List;

public class UpdatePricesController {


    private FetchProductInteractor productsInteractor;
    private FetchSingleInteractor singleInteractor;
    private WebScraper webScraper;
    private ConnPL connPL;


    public UpdatePricesController() {

        ApiScryfall apiScryfall = new ApiScryfall();
        connPL = new ConnPL();
        this.singleInteractor = new FetchSingleInteractor(apiScryfall);
        this.productsInteractor = new FetchProductInteractor(connPL);

    }

    public void updatePriceForGivenProduct(List<String> skus){

        List<ProductResponseModel> productResponseModelList = new ArrayList<>();
        for(int i=0; i<skus.size(); i++){
            Product product = new Product();
            product.setSku(skus.get(i));
            productResponseModelList.add(new ProductResponseModel(product));
            System.out.println(skus.get(i));
        }

        List<SingleResponseModel> singleResponseModelList = fetchNewSingleinfo(productResponseModelList);
        System.out.println(singleResponseModelList.get(2).getSingle().getCondition());
        List<ProductResponseModel> productResponseModelListUpdate = fetchNewPriceSingleIntegration(singleResponseModelList);
        connPL = new ConnPL();
        for (ProductResponseModel productResponseModel : productResponseModelListUpdate) {
            System.out.println("Product to Update: " +
                    productResponseModel.getProduct().getSku() + " - name:" +
                    productResponseModel.getProduct().getNombre() + " - price:" +
                    productResponseModel.getProduct().getPrecioNormal()
            );
            connPL.updateMagicCardPrice(productResponseModel);
        }
        System.out.println("done");
    }

    private List<SingleResponseModel> fetchNewSingleinfo(List<ProductResponseModel> productResponseModelList){
        List<SingleResponseModel> singleResponseModelList = new ArrayList<>();
        for (ProductResponseModel productResponseModel : productResponseModelList) {
            singleResponseModelList.add(singleInteractor.fetch(productResponseModel));
        }

        return singleResponseModelList;
    }


    public void updatePriceInPL() {

        List<SingleResponseModel> singleResponseModelList = fetchNewSingleInfoIntegration();
        List<ProductResponseModel> productResponseModelListUpdate = fetchNewPriceSingleIntegration(singleResponseModelList);
        connPL = new ConnPL();
        for (ProductResponseModel productResponseModel : productResponseModelListUpdate) {
            System.out.println("Product to Update: " +
                    productResponseModel.getProduct().getSku() + " - name:" +
                    productResponseModel.getProduct().getNombre() + " - price:" +
                    productResponseModel.getProduct().getPrecioNormal()
            );
            connPL.updateMagicCardPrice(productResponseModel);
        }
        System.out.println("done");
    }



    private List<SingleResponseModel> fetchNewSingleInfoIntegration() {

        List<ProductResponseModel> productResponseModelList = productsInteractor.getCardsStockPL();
        List<SingleResponseModel> singleResponseModelList = new ArrayList<>();
        for (ProductResponseModel productResponseModel : productResponseModelList) {
            singleResponseModelList.add(singleInteractor.fetch(productResponseModel));
        }

        return singleResponseModelList;
    }


    private List<ProductResponseModel> fetchNewPriceSingleIntegration(List<SingleResponseModel> singleResponseModelList) {

        webScraper = new WebScraper();

        List<SingleResponseModel> singleResponseModelListFromScrap = new ArrayList<>();
        List<SingleResponseModel> singleResponseModelsFromPL = new ArrayList<>(singleResponseModelList);


        webScraper.setExpansionIDForCK(singleResponseModelsFromPL.get(0).getSingle().getExpansionName());
        String checkExpansionCodeBefore = singleResponseModelsFromPL.get(0).getSingle().getExpansionName();

        for (int i = 0; i < singleResponseModelsFromPL.size(); i++) {

            Single single = setSingle(singleResponseModelsFromPL, i);

            if (single.getExpansionName().equals(checkExpansionCodeBefore)) {
                checkExpansionCodeBefore = single.getExpansionName();
            } else {
                webScraper.setExpansionIDForCK(single.getExpansionName());
                checkExpansionCodeBefore = single.getExpansionName();

                if(webScraper.expansionID_CK == 0){
                    System.out.println("get the new code for " + single.getExpansionCode());
                    System.out.println("new code expansion");
                    Expansion expansion = new Expansion();
                    webScraper.expansionID_CK = expansion.getExpansionID(single.getExpansionCode());
                }
            }

            SingleResponseModel singleResponseModelFromScrap = singleInteractor.setPriceFromCardSite(single, webScraper);
            singleResponseModelListFromScrap.add(singleResponseModelFromScrap);
        }


        List<ProductResponseModel> productResponseModelListUpdate = productsInteractor.productsToUpdate(
                singleResponseModelListFromScrap, singleResponseModelsFromPL);


        List<ProductResponseModel> productsHasError = productsInteractor.checkEmptyPrice(productResponseModelListUpdate);
        productResponseModelListUpdate = writeProductError(productsHasError,productResponseModelListUpdate);


        return productResponseModelListUpdate;
    }

    //TODO: a better way to copy?
    private Single setSingle(List<SingleResponseModel> singleResponseModelsFromPL, int i) {
        Single single = new Single();
        single.setPrice(singleResponseModelsFromPL.get(i).getSingle().getPrice());
        single.setRarity(singleResponseModelsFromPL.get(i).getSingle().getRarity());
        single.setCondition(singleResponseModelsFromPL.get(i).getSingle().getCondition());
        single.setFoil(singleResponseModelsFromPL.get(i).getSingle().isFoil());
        single.setLang(singleResponseModelsFromPL.get(i).getSingle().getLang());
        single.setName(singleResponseModelsFromPL.get(i).getSingle().getName());
        single.setExpansionName(singleResponseModelsFromPL.get(i).getSingle().getExpansionName());
        single.setExpansionCode(singleResponseModelsFromPL.get(i).getSingle().getExpansionCode());
        single.setCollectorNumber(singleResponseModelsFromPL.get(i).getSingle().getCollectorNumber());

        return single;
    }

    private List<ProductResponseModel> writeProductError(List<ProductResponseModel> productsHasError,
                                                         List<ProductResponseModel> productResponseModelListUpdate) {
        if (productsHasError.size() > 0) {
            Writer writer = new Writer();
            String filename = "products_with_error";
            writer.errorProductWriter(productsHasError, filename);
            System.out.println("products with errors");
            return productsInteractor.removeEmptyPrice(productResponseModelListUpdate);
        }
        return productResponseModelListUpdate;
    }


}
