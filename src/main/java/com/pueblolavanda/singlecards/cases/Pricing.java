package com.pueblolavanda.singlecards.cases;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Pricing {

    private static final BigDecimal dolarToCLP = new BigDecimal("950");
    private static final BigDecimal transbankCommission = new BigDecimal("5");

    private static final BigDecimal BULK_COMMON_PRICE = new BigDecimal("150");
    private static final BigDecimal BULK_UNCOMMON_PRICE = new BigDecimal("500");
    private static final BigDecimal BULK_RARE_PRICE = new BigDecimal("900");
    private static final BigDecimal BULK_MYTHIC_PRICE = new BigDecimal("990");

    private static final BigDecimal BULK_COMMON_PRICE_USD = new BigDecimal("0.25");
    private static final BigDecimal BULK_UNCOMMON_PRICE_USD = new BigDecimal("0.52");
    private static final BigDecimal BULK_RARE_PRICE_USD = new BigDecimal("0.8");
    private static final BigDecimal BULK_MYTHIC_PRICE_USD = new BigDecimal("1");


    private static final BigDecimal EX_PERCENTAGE = new BigDecimal("15");
    private static final BigDecimal VG_PERCENTAGE = new BigDecimal("30");
    private static final BigDecimal G_PERCENTAGE = new BigDecimal("50");



    private static BigDecimal roundPriceToPL(BigDecimal usdPrice) {
        BigDecimal clp = usdToCLP(usdPrice);
        return addTransbankCommission(clp);
    }


    public static BigDecimal convertPriceToPL(BigDecimal usdPrice, String rarity, String condition){
        if((rarity.toLowerCase().equals("common")) && (usdPrice.compareTo(BULK_COMMON_PRICE_USD) <= 0)){
            return bulkCardPrice(rarity,condition);
        }
        else if((rarity.toLowerCase().equals("uncommon")) && (usdPrice.compareTo(BULK_UNCOMMON_PRICE_USD) <= 0)){
            return bulkCardPrice(rarity,condition);
        }
        else if((rarity.toLowerCase().equals("rare")) && (usdPrice.compareTo(BULK_RARE_PRICE_USD) <= 0)){
            return bulkCardPrice(rarity,condition);
        }
        else if((rarity.toLowerCase().equals("mythic")) && (usdPrice.compareTo(BULK_MYTHIC_PRICE_USD) <= 0)){
            return bulkCardPrice(rarity,condition);
        }
        return roundPriceToPL(usdPrice);
    }


    private static BigDecimal usdToCLP(BigDecimal price){
        price = price.multiply(dolarToCLP);
        price = new BigDecimal(price.toString()).setScale(0, RoundingMode.UP);
        String priceConverted = price.toString().trim();
        String checkLastNumber =  priceConverted.substring(priceConverted.length() - 1);
        if(Integer.parseInt(checkLastNumber) <= 5){
            priceConverted = priceConverted.substring(0,priceConverted.length() - 1) + "0";
        }
        else
        {
            priceConverted = priceConverted.substring(0,priceConverted.length() - 1) + "0";
            int roundUpPrice = Integer.parseInt(priceConverted) + 10;
            priceConverted = String.valueOf(roundUpPrice);
        }

        return new BigDecimal(priceConverted);
    }

    private static BigDecimal bulkCardPrice(String rarity, String condition){
        switch(rarity){
            case "common":
                return conditionPrice(BULK_COMMON_PRICE, condition);
            case "uncommon":
                return conditionPrice(BULK_UNCOMMON_PRICE, condition);
            case "rare":
                return conditionPrice(BULK_RARE_PRICE, condition);
            case "mythic":
                return conditionPrice(BULK_MYTHIC_PRICE, condition);
        }
        return new BigDecimal("0");
    }

    private static BigDecimal conditionPrice(BigDecimal usdPrice, String condition){
        BigDecimal totalPercentage = new BigDecimal("100");
        switch(condition.toUpperCase()){
            case "EX":
                return addTransbankCommission(usdPrice.multiply(totalPercentage.subtract(EX_PERCENTAGE)).divide(totalPercentage));
            case "VG":
                return addTransbankCommission(usdPrice.multiply(totalPercentage.subtract(VG_PERCENTAGE)).divide(totalPercentage));
            case "G":
                return addTransbankCommission(usdPrice.multiply(totalPercentage.subtract(G_PERCENTAGE)).divide(totalPercentage));
            default:
                return addTransbankCommission(usdPrice);
        }
    }

    private static BigDecimal addTransbankCommission(BigDecimal price){
        BigDecimal total = new BigDecimal("100");
        BigDecimal toAdd = price.multiply(total);
        BigDecimal commision = total.subtract(transbankCommission);
        price = toAdd.divide(commision,BigDecimal.ROUND_HALF_UP);
        BigDecimal scaled = price.setScale(0, RoundingMode.HALF_UP);
        return new BigDecimal(scaled.toString());
    }

}
