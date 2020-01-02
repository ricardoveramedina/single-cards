package com.pueblolavanda.singlecards.cases;

import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Face;
import com.pueblolavanda.singlecards.domain.Product;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.pueblolavanda.singlecards.StringHelper.cleanFilename;
import static com.pueblolavanda.singlecards.cases.Pricing.*;
import static com.pueblolavanda.singlecards.StringHelper.getLanguage;

@Component
public class FetchCSVLoaderInteractor {

    private static final String siteFolderUpload = "https://pueblolavanda.cl/wp-content/uploads/";
    private static final String SIMPLE = "Simple";
    private static final String VISIBLE = "visible";
    private static final String NONE = "none";
    private static final String CONDITION_NM = "NM";
    private final String actualMonth = getActualMonth();
    private final String actualYear = getActualYear();
    private String lang = "en";



    public CSVLoaderResponseModel fetchNewCardProducts(ExpansionResponseModel expansionResponseModel) {

        List<Product> products = new ArrayList<>();

        if (expansionResponseModel.getExpansion().getCards().isEmpty()) {
            return new CSVLoaderResponseModel(products);
        }

        for (Card card : expansionResponseModel.getExpansion().getCards()) {

            Product product = new Product();

            String nameExpansion = expansionResponseModel.getExpansion().getName();
            String codeExpansion = expansionResponseModel.getExpansion().getCode();

            lang = expansionResponseModel.getExpansion().getCards().get(0).getLang();

            //SKU = M(magic) + numero de carta + estado + expansion + es foil? 1-M-xln-1-es
            product.setSku("M-"+ card.getCollectorNumber() + "-"+CONDITION_NM+"-" + codeExpansion + "-" + 0 + "-" + lang);

            product.setTipo(SIMPLE);
            product.setPublicado("1");
            product.setEstaDestacado(false);
            product.setVisibilidadCatalogo(VISIBLE);
            product.setEstadoImpuesto(NONE);
            product.setEnInventario(true);



            if (card.getLayout().contains("split") || card.getLayout().contains("flip"))
            {
                setSplitCardData(card, product, nameExpansion);
            }
            else if (card.getLayout().contains("transform")){
                setFaceCardData(card, product, nameExpansion);
            }
            else
            {
                setNormalCardData(card, product, nameExpansion);
            }

            BigDecimal price = convertPriceToPL(card.getPriceCK(),card.getRarity(),CONDITION_NM);

            product.setPrecioNormal(price);
            product.setCategorias(getCategoriasProduct(nameExpansion));
            product.setEtiquetas(getEtiquetasProduct(nameExpansion,card));

            products.add(product);
        }

        return new CSVLoaderResponseModel(products);
    }

    private String getCategoriasProduct(String nameExpansion) {
        return "Magic, Magic > Expansión, Magic > Expansión > "+ nameExpansion + ", "
                                + "Magic > Singles, "
                                + "Magic > Singles > Expansión > " + nameExpansion;
    }

    private String getEtiquetasProduct(String nameExpansion, Card card){

        String formats = "";
        Set<String> keys = card.getLegalities().keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            boolean value = card.getLegalities().get(key);
            if(value) formats += key + ", ";
        }

        return  "Carta Suelta, "+ nameExpansion + ", Magic, " + formats + card.getRarity();
    }


    private String setDataSpanish(Card card, Product product, String description,String filePath){

        if(card.getLayout().contains("transform")){
            return setDataSpanishFace(card, product, description, filePath);
        }
        else if(card.getLayout().contains("split")){
            return setDataSpanishSplit(card, product, description, filePath);
        }
        else{
            return setDataSpanishNormal(card, product, description, filePath);
        }

    }

    private void setNormalCardData(Card card, Product product, String nameExpansion) {

        String filePath = getFilePath(actualMonth, actualYear, card.getCollectorNumber(), card.getName(), nameExpansion);
        product.setImagenes(filePath);

        String description = "Estado: "+ CONDITION_NM + "<br>" +
                "Idioma de la carta: " + getLanguage(lang) + "<br>" +
                "Tipo de carta: " + card.getTypeLine() + "<br>" +
                "Oracle: " + card.getOracle() + "<br>" +
                "Costo de Mana: " + card.getManaCost() + "<br>" +
                "Rareza: " + card.getRarity() + "<br>" +
                "Expansion: " + nameExpansion + "<br>" +
                "Ilustrador: " + card.getArtistName();
        if(!card.getNameEs().isEmpty()){
            description = setDataSpanish(card,product,description,filePath);
        }
        else{
            String name = card.getName();
            product.setNombre(name);
            description += "<br> " + setImgHtml(filePath,name);
        }
        description = description.replaceAll(System.lineSeparator(),"<br>");
        product.setDescripcion(description);
        product.setDescripcionCorta(product.getNombre() + " - " + nameExpansion + " - " +
                card.getRarity() + "-" + CONDITION_NM + "-" + getLanguage(lang));

    }



    private String setDataSpanishNormal(Card card, Product product, String description, String filePath) {
        String name;
        String descriptionSpanish;
        name = card.getName() + " - " + card.getNameEs();
        product.setNombre(name);
        descriptionSpanish = "<br>-------------------------------------------------------------<br>" +
                "<br>" + getLanguage(lang) + " - " + "<br>" +
                "Tipo de carta: " + card.getTypeLineEs() + "<br>" +
                "Oracle: " + card.getOracleEs() + "<br>" +
                "Flavor: " + card.getFlavorText() + "<br> " +
                setImgHtml(filePath,name);

        return description + descriptionSpanish;
    }

    private void setSplitCardData(Card card, Product product, String nameExpansion){

        String filePath = getFilePath(actualMonth, actualYear, card.getCollectorNumber(), card.getName(), nameExpansion);
        product.setImagenes(filePath);

        String description = "Estado: "+ CONDITION_NM + "<br>" +
                "Idioma de la carta: " + getLanguage(lang) + "<br>" +
                "Tipo de carta: " + card.getTypeLine() + "<br>";
        for(Face face : card.getFaces()) {
            description += "Oracle: " + face.getOracleText() + "<br>" +
                    "Costo de Mana: " + face.getManaCost() + "<br>";
        }
        description += "Rareza: " + card.getRarity() + "<br>" +
                "Expansion: " + nameExpansion + "<br>" +
                "Ilustrador: " + card.getArtistName();

        if(!card.getFaces().get(0).getNameEs().isEmpty()){
            description = setDataSpanishSplit(card,product,description,filePath);
        }
        else{
            String name = card.getName();
            product.setNombre(name);
            description += "<br> " + setImgHtml(filePath,name);
        }
        description = description.replaceAll(System.lineSeparator(),"<br>");
        product.setDescripcion(description);
        product.setDescripcionCorta(product.getNombre() + " - " + nameExpansion + " - " +
                card.getRarity() + " - " + CONDITION_NM + " - " + getLanguage(lang));
    }



    private String setDataSpanishSplit(Card card, Product product, String description, String filePath) {
        String name;
        name = card.getName() + " - " + card.getFaces().get(0).getNameEs() + " // " +
                card.getFaces().get(1).getNameEs();
        product.setNombre(name);
        String descriptionSpanish = "<br>-------------------------------------------------------------<br>" +
                                    "<br>" + getLanguage(lang) + " - " + "<br>";
        for(Face face : card.getFaces()) {
            descriptionSpanish += "Tipo de carta: " + face.getTypeLineEs() +  "<br>" +
                    "Oracle: " + face.getOracleEs() +  "<br>" +
                    "Flavor: " + face.getFlavorText() + "<br>";
        }
        descriptionSpanish += setImgHtml(filePath,name);

        return description + descriptionSpanish;
    }



    private void setFaceCardData(Card card, Product product, String nameExpansion) {

        String filePath = getFilePath(actualMonth, actualYear, card.getCollectorNumber(), card.getFaces().get(0).getName(), nameExpansion);
        product.setImagenes(filePath);

        String description = "Estado: "+ CONDITION_NM + "<br>" +
                "Idioma de la carta: " + getLanguage(lang) + "<br>";
        for(int i= 0; card.getFaces().size() > i; i++){
            Face face = card.getFaces().get(i);
            description += "Tipo de carta: " + face.getTypeLine() + "<br>" +
                    "Oracle: " + face.getOracleText() + "<br>" +
                    "Costo de Mana: " + face.getManaCost() + "<br>" +
                    "Rareza: " + card.getRarity() + "<br>" +
                    "Expansion: " + nameExpansion + "<br>" +
                    "Ilustrador: " + face.getArtist();
            if(i==0){
                description += "<br>Transform:<br>-------------------------------------------------------------<br>";
            }
        }

        if(!card.getFaces().get(0).getNameEs().isEmpty()){
            description = setDataSpanishFace(card,product,description,nameExpansion);
        }
        else{
            String name = card.getName();
            product.setNombre(name);
            for(Face face : card.getFaces()) {
                filePath = getFilePath(actualMonth, actualYear,card.getCollectorNumber(),face.getName(),nameExpansion);
                description += "<br> " + setImgHtml(filePath, face.getName());
            }
        }

        product.setDescripcionCorta(product.getNombre() + " - " + nameExpansion + " - " +
                card.getRarity() + "-" + CONDITION_NM + "-" + getLanguage(lang));
        description = description.replaceAll(System.lineSeparator(),"<br>");
        product.setDescripcion(description);
    }

    private String setDataSpanishFace(Card card, Product product, String description,String nameExpansion) {

        String name = card.getName() + " - " + card.getFaces().get(0).getNameEs() + " // " +
                card.getFaces().get(1).getNameEs();
        product.setNombre(name);
        String descriptionSpanish = "<br>-------------------------------------------------------------<br>" +
                "<br>" + getLanguage(lang) + " - " + "<br>";

        for(int i= 0; card.getFaces().size() > i; i++){
            Face face = card.getFaces().get(i);
            descriptionSpanish +=
                    "Tipo de carta: " + face.getTypeLineEs() + "<br>" +
                    "Oracle: " + face.getOracleEs() + "<br>" +
                    "Flavor: " + face.getFlavorText() + "<br>";
            if(i==0){
                descriptionSpanish += "<br>Transform:<br>-------------------------------------------------------------<br>";
            }
        }
        for(Face face : card.getFaces()) {
            String filePath = getFilePath(actualMonth, actualYear,card.getCollectorNumber(),face.getName(),nameExpansion);
            descriptionSpanish += "<br> " + setImgHtml(filePath, face.getName());
        }

        return description + descriptionSpanish;
    }


    private String getFilePath(String actualMonth, String actualYear, String collectorNumber, String cardName, String nameExpansion) {
        String filePath = getImageFilePath(collectorNumber, cardName, nameExpansion);
        return siteFolderUpload + actualYear + "/" + actualMonth + "/" + cleanFilename(filePath);
    }


    private String getImageFilePath(String collectorNumber, String cardName, String nameExpansion) {
        return nameExpansion + "_" + collectorNumber + "_" +
                cleanFilename(cardName) + ".jpg";
    }


    private String getActualMonth() {
        return String.valueOf(String.format("%02d", LocalDate.now().getMonthValue()));
    }

    private String getActualYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    private String setImgHtml(String filePath, String name){
        name = name.replaceAll("'", "").replaceAll("\"", "");
        return "<img width='245' height='342' src='" + filePath + "' class='wp-post-image' alt='" + name +
                "' title='" + name + "'>";
    }


}
