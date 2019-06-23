package com.example.pricey.currency;

import java.util.HashMap;
import java.util.Map;

public class Rate {
    public float ILS;
    public float EUR;
    public float USD;
    public float AUD;
    public float CAD;
    public float GBP;
    public float JPY;
    public float RUB;

    public float getConversionRate(String baseRateName, String targetRateName) {
        Map<String,Float> rates = new HashMap<String,Float>();
        rates.put("ILS",ILS);
        rates.put("EUR",EUR);
        rates.put("USD",USD);
        rates.put("AUD",AUD);
        rates.put("CAD",CAD);
        rates.put("GBP",GBP);
        rates.put("JPY",JPY);
        rates.put("RUB",RUB);

        Float baseRate = rates.get(baseRateName);
        Float targetRate = rates.get(targetRateName);

        if (baseRate == null) {
            baseRate = 1.0f;
        }
        if (targetRate == null) {
            targetRate = 1.0f;
        }

        return targetRate / baseRate;
    }
    public String[] getCurrencyNames(){
        String[] currencyArray = {"ILS","EUR","USD","AUD","CAD","GBP","JPY","RUB"};
        return currencyArray;
    }
}
