package de.hilbert.securities.utils;

import java.util.Calendar;
import java.util.Map;

/**
 * Utils class for calculating P/E ratio / PER / KGV
 *
 * @author Ralf Hilbert
 * @since 11.01.2019
 */
public class PriceEarningsRatio {

    private PriceEarningsRatio() {
    }

    public static float calculateGrahamPER(Map<Integer, Float> earningsPerStockAndYearAfterTax, float price) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        float summedEarnings = sumYearValues(earningsPerStockAndYearAfterTax, 3, currentYear);

        // TODO: 11.01.2019 implement propper error handling - possible division by 0
        return price / (summedEarnings / 3);
    }

    private static float sumYearValues(Map<Integer, Float> yearValueMap, int countOfSummedYears, int startYear) {
        if (countOfSummedYears == 0 || startYear == 2000) {
            return 0F;
        }
        if (yearValueMap.containsKey(startYear)) {
            return yearValueMap.get(startYear) + sumYearValues(yearValueMap, countOfSummedYears - 1, startYear - 1);
        }
        return sumYearValues(yearValueMap, countOfSummedYears, startYear - 1);
    }
}
