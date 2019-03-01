package de.hilbert.securities.services;

import de.hilbert.securities.exceptions.IORuntimeException;
import de.hilbert.securities.exceptions.NotYetImplementedException;
import de.hilbert.securities.models.Security;
import de.hilbert.securities.utils.PriceEarningsRatio;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Ralf Hilbert
 * @since 20.12.2018
 */
@Service
public class SecurityEnrichmentService {

    private static final String URL_ARIVA = "https://www.ariva.de/{isin}/bilanz-guv";
    private Logger log = LoggerFactory.getLogger(SecurityEnrichmentService.class);

    public Security enrich(Security security) throws IOException {

        Document document = null;

        String urlToScrape = URL_ARIVA.replaceAll("\\{isin\\}", security.getISIN());

        long start = System.currentTimeMillis();
        document = Jsoup.connect(urlToScrape).get();
        long end = System.currentTimeMillis();
        // TODO: 01.03.2019 this is no value log
        log.info("needed " + (end - start) + " ms to scrape " + document.html().getBytes(StandardCharsets.UTF_8).length / 1024 + " kb from " + urlToScrape);


        validateDocument(document);

        security.setPrice(floatOf(document.select("span[itemprop=price]").text()));

        Map<String, Map<Integer, Float>> rawData = parseFundamentalData(document);

        Map<String, Map<Integer, Float>> rawDataHisoty1 = parseFundamentalData(Jsoup.connect(urlToScrape + "?page=6").get());
        Map<String, Map<Integer, Float>> rawDataHisoty2 = parseFundamentalData(Jsoup.connect(urlToScrape + "?page=12").get());
        Map<String, Map<Integer, Float>> rawDataHisoty3 = parseFundamentalData(Jsoup.connect(urlToScrape + "?page=18").get());

        rawDataHisoty1.forEach((key, integerFloatMap) -> rawData.merge(key, integerFloatMap, this::mergeInternalMap));
        rawDataHisoty2.forEach((key, integerFloatMap) -> rawData.merge(key, integerFloatMap, this::mergeInternalMap));
        rawDataHisoty3.forEach((key, integerFloatMap) -> rawData.merge(key, integerFloatMap, this::mergeInternalMap));

        security.setRawData(rawData);

        security.setEarningsPerStockAndYearAfterTax(rawData.get("Ergebnis je Aktie (verwässert)"));
        security.setGrahamPER(PriceEarningsRatio.calculateGrahamPER(security.getEarningsPerStockAndYearAfterTax(), security.getPrice()));

        return security;
    }

    private Map<Integer, Float> mergeInternalMap(Map<Integer, Float> integerFloatMap1, Map<Integer, Float> integerFloatMap2) {
        Map<Integer, Float> mergedMap = new TreeMap<>(integerFloatMap1);
        integerFloatMap2.forEach((integer, aFloat) -> mergedMap.merge(integer, aFloat, (aFloat1, aFloat2) -> aFloat1));
        return mergedMap;
    }

    private Map<String, Map<Integer, Float>> parseFundamentalData(Document document) {
        Map<String, Map<Integer, Float>> map = new HashMap<>();

        fillMapWithTableContent(document, map, "#pageFundamental > div.tabelleUndDiagramm.aktie.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");
        fillMapWithTableContent(document, map, "#pageFundamental > div.tabelleUndDiagramm.guv.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");
        fillMapWithTableContent(document, map, "#pageFundamental > div.tabelleUndDiagramm.personal.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");
        fillMapWithTableContent(document, map, "#pageFundamental > div.tabelleUndDiagramm.bewertung.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");
        return map;
    }

    private void fillMapWithTableContent(Document document, Map<String, Map<Integer, Float>> map, String tableSelector) {
        for (int i = 1; i < 20; i++) {
            String titleOfLine = document.select(tableQuery(i, 1, tableSelector)).text();
            if (titleOfLine.isEmpty()) {
                // nothing to parse within this line
                continue;
            }

            Map<Integer, Float> yearValueMap = new TreeMap<>();
            for (int j = 2; j < 8; j++) {
                String cellOfTable = document.select(tableQuery(i, j, tableSelector)).text();
                if (cellOfTable.isEmpty() || cellOfTable.equals(" ") || cellOfTable.equals("-  ")) {
                    // not parsable cell of table table cell
                    continue;
                }

                String year = document.select(tableQuery(1, j, tableSelector)).text();
                yearValueMap.put(Integer.valueOf(year), floatOf(cellOfTable));

            }
            map.put(titleOfLine, yearValueMap);
        }
    }

    private void validateDocument(Document document) {
        if (document == null) {
            throw new IORuntimeException("No document available.");
        } else if (document.text().contains("Keine Fundamentaldaten verfügbar")) {
            throw new NotYetImplementedException("Securities of this type are not yet supported");
        }
    }

    private float floatOf(String string) {
        return Float.valueOf(string.replaceAll("\\.", "").replace(",", "."));
    }

    private String tableQuery(int row, int column, String selector) {
        return selector.replaceAll("\\{row\\}", String.valueOf(row)).replaceAll("\\{column\\}", String.valueOf(column));
    }
}
