package com.pueblolavanda.singlecards;

public class StringHelper {

    public static String cleanFilename(String filename){
        return cleanChars(filename).toLowerCase()
                .replaceAll(" ", "_")
                .replaceAll("'","");
    }
    //TODO: check if work anthology 2 https://www.CardSite.com/mtg/commander-anthology-vol-ii/damia-sage-of-stone-foil
    private static String cleanChars(String filename){
        if(filename.matches(".+?\\d+([\\d,]?\\d)*(\\.\\d+)?.+?"))
        {
            return replaceSome(filename);
        }
        else{
            return replaceSome(filename).replaceAll(",","");
        }
    }

    private static String replaceSome(String filename){
        return filename.replaceAll("á","a")
                .replaceAll("é","e")
                .replaceAll("í","i")
                .replaceAll("ú","u")
                .replaceAll("û","u")
                .replaceAll("ó","o")
                .replaceAll(" // ", "-");
    }

    public static String cleanFilenameURL(String filename){
        return cleanChars(filename).toLowerCase();
    }


    //TODO: move to StringHelper
    private enum LanguageCode {
        EN,ES,JA
    }

    public static String getLanguage(String lang) {
        LanguageCode language = LanguageCode.valueOf(lang.toUpperCase());
        switch (language){
            case ES: return "Español";
            case EN: return "Inglés";
            case JA: return "Japonés";
        }
        return "";
    }

}
