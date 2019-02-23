package de.hilbert.securities.models;

import java.util.Map;

/**
 * @author Ralf Hilbert
 * @since 20.12.2018
 */
public class Security implements DataTransferObject {

    private String ISIN;
    private float price;
    private Map<Integer, Float> earningsPerStockAndYearAfterTax;
    private float grahamPER;

    public Map<String, Map<Integer, Float>> getRawData() {
        return rawData;
    }

    public void setRawData(Map<String, Map<Integer, Float>> rawData) {
        this.rawData = rawData;
    }

    private Map<String, Map<Integer, Float>> rawData;

    public Security(String ISIN) {
        this.ISIN = ISIN;
    }

    public String getISIN() {
        return ISIN;
    }

    public void setISIN(String ISIN) {
        this.ISIN = ISIN;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Map<Integer, Float> getEarningsPerStockAndYearAfterTax() {
        return earningsPerStockAndYearAfterTax;
    }

    public void setEarningsPerStockAndYearAfterTax(Map<Integer, Float> earningsPerStockAndYearAfterTax) {
        this.earningsPerStockAndYearAfterTax = earningsPerStockAndYearAfterTax;
    }

    public float getGrahamPER() {
        return grahamPER;
    }

    public void setGrahamPER(float grahamPER) {
        this.grahamPER = grahamPER;
    }
}
