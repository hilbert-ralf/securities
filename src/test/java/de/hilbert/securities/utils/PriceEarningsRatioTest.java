package de.hilbert.securities.utils;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ralf Hilbert
 * @since 11.01.2019
 */
class PriceEarningsRatioTest {

    @Test
    void testCalculateGrahamPER() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Map<Integer, Float> earningsPerStockAndYearAfterTax = new LinkedHashMap<>();
        earningsPerStockAndYearAfterTax.put(currentYear - 1, 5f);
        earningsPerStockAndYearAfterTax.put(currentYear - 2, 5f);
        earningsPerStockAndYearAfterTax.put(currentYear - 3, 5f);

        float grahamPRE = PriceEarningsRatio.calculateGrahamPER(earningsPerStockAndYearAfterTax, 50);

        assertEquals(10f, grahamPRE);
    }
}