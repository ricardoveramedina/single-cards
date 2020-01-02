package com.pueblolavanda.singlecards.controllers;

import com.pueblolavanda.singlecards.cases.CSVLoaderResponseModel;
import com.pueblolavanda.singlecards.drivers.db.ConnPL;

public class UpdateDataController {

    private ConnPL connPL;

    public UpdateDataController(){
        connPL = new ConnPL();
    }

    public void insertSeo(CSVLoaderResponseModel csvLoaderResponseModel){
        boolean status = connPL.insertYoastSeo(csvLoaderResponseModel);
        if(!status) System.out.println("Error");
    }

    public void updateShortDescription(String languageCode, String condition){
        boolean status = connPL.updateShortDescription(languageCode, condition);
        if(!status) System.out.println("Error");
    }


}
