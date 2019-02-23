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

/**
 * @author Ralf Hilbert
 * @since 20.12.2018
 */
@Service
public class SecurityEnrichmentService {

    private static final String URL_ARIVA = "https://www.ariva.de/{isin}/bilanz-guv";
    private Logger log = LoggerFactory.getLogger(SecurityEnrichmentService.class);

    public Security enrich(Security security) {

        Document document = null;

        try {
            String urlToScrape = URL_ARIVA.replaceAll("\\{isin\\}", security.getISIN());
            long start = System.currentTimeMillis();
            document = Jsoup.connect(urlToScrape).get();
            long end = System.currentTimeMillis();
            log.info("needed " + (end - start) + " ms to scrape " + document.html().getBytes(StandardCharsets.UTF_8).length / 1024 + " kb from " + urlToScrape);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        validateDocument(document);

        security.setPrice(floatOf(document.select("span[itemprop=price]").text()));

        Map<String, Map<Integer, Float>> map = new HashMap<>();

        parseTable(document, map, "#pageFundamental > div.tabelleUndDiagramm.aktie.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");
        parseTable(document, map, "#pageFundamental > div.tabelleUndDiagramm.guv.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");
        parseTable(document, map, "#pageFundamental > div.tabelleUndDiagramm.personal.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");
        parseTable(document, map, "#pageFundamental > div.tabelleUndDiagramm.bewertung.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})");

        security.setRawData(map);

        security.setEarningsPerStockAndYearAfterTax(map.get("Ergebnis je Aktie (verwässert)"));
        security.setGrahamPER(PriceEarningsRatio.calculateGrahamPER(security.getEarningsPerStockAndYearAfterTax(), security.getPrice()));

        return security;
    }

    private void parseTable(Document document, Map<String, Map<Integer, Float>> map, String tableSelector) {
        for (int i = 1; i < 20; i++) {
            String titleOfLine = document.select(tableQuery(i, 1, tableSelector)).text();
            if (titleOfLine.isEmpty()) {
                // nothing to parse within this line
                continue;
            }

            Map<Integer, Float> yearValueMap = new LinkedHashMap<>();
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
