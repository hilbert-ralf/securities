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

        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "3128");

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

        int earningsRow = 0;
        //"Ergebnis je Aktie (verwässert)" not in the same row every time, we need to find the row
        for (int i = 1; i < 15; i++) {
            String earningsLine = document.select(earningsTableQuery(i, 1)).text();
            if (earningsLine.startsWith("Ergebnis je Aktie (verwässert)")) {
                earningsRow = i;
                break;
            }
        }

        if (earningsRow > 0) {
            Map<Integer, Float> earningsPerYear = new LinkedHashMap<>();
            for (int i = 2; i < 8; i++) {
                String earning = document.select(earningsTableQuery(earningsRow, i)).text();
                if (!earning.equals("-  ")) {
                    String year = document.select(earningsTableQuery(1, i)).text();
                    earningsPerYear.put(Integer.valueOf(year), floatOf(earning));
                }
            }
            security.setEarningsPerStockAndYearAfterTax(earningsPerYear);

            security.setGrahamPER(PriceEarningsRatio.calculateGrahamPER(security.getEarningsPerStockAndYearAfterTax(), security.getPrice()));
        }


        return security;
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

    private String earningsTableQuery(int row, int column) {
        String earningsTableQuery = "#pageFundamental > div.tabelleUndDiagramm.aktie.new.abstand > div.column.twothirds.table > table > tbody > tr:nth-child({row}) > td:nth-child({column})";
        return earningsTableQuery.replaceAll("\\{row\\}", String.valueOf(row)).replaceAll("\\{column\\}", String.valueOf(column));
    }
}
