package de.hilbert.securities.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author Ralf Hilbert
 * @since 22.02.2019
 */
@Component
@ConditionalOnProperty(name = "behind.proxy", havingValue = "true")
public class ProxyConfig {

    public ProxyConfig() {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "3128");
    }
}
