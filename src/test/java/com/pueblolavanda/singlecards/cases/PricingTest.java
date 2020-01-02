package com.pueblolavanda.singlecards.cases;

import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

import static com.pueblolavanda.singlecards.cases.Pricing.*;


import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

public class PricingTest {

    public static final String RARE = "rare";
    public static final String COMMON = "common";

    @Test
    public void roundPriceToPLTest(){
        BigDecimal usdPrice = new BigDecimal("0.3");
        assertThat(convertPriceToPL(usdPrice, RARE, "NM"),is(new BigDecimal("842")));
        assertThat(convertPriceToPL(usdPrice, RARE, "EX"),is(new BigDecimal("716")));
        assertThat(convertPriceToPL(usdPrice, RARE, "VG"),is(new BigDecimal("589")));
        assertThat(convertPriceToPL(usdPrice, RARE, "G"),is(new BigDecimal("421")));
    }

    @Test
    public void checkCommonPriceEXTest(){
        BigDecimal usdPrice = new BigDecimal("0.20");
        assertThat(convertPriceToPL(usdPrice, COMMON, "EX"),is(new BigDecimal("134")));
    }

    @Test
    public void checkCommonPriceVGTest(){
        BigDecimal usdPrice = new BigDecimal("0.90");
        assertThat(convertPriceToPL(usdPrice, COMMON, "VG"),is(new BigDecimal("111")));
    }

    @Test
    public void checkCommonPriceGTest(){
        BigDecimal usdPrice = new BigDecimal("0.20");
        assertThat(convertPriceToPL(usdPrice, COMMON, "G"),is(new BigDecimal("79")));
    }


    @Test
    public void checkIfPriceIsLowTest(){
        BigDecimal usdPrice = new BigDecimal("1.97");
        System.out.println(convertPriceToPL(usdPrice, RARE,"G"));
    }

    @Ignore
    @Test
    public void usdToCLPTest() {
    }

    @Ignore
    @Test
    public void addTransbankCommissionTest() {
    }
}