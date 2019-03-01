package de.hilbert.securities.controller;

import de.hilbert.securities.exceptions.IORuntimeException;
import de.hilbert.securities.exceptions.NotYetImplementedException;
import de.hilbert.securities.models.DataTransferObject;
import de.hilbert.securities.models.Error;
import de.hilbert.securities.models.Security;
import de.hilbert.securities.services.SecurityEnrichmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Ralf Hilbert
 * @since 20.12.2018
 */
@RestController
public class SecuritiesController {

    private Logger log = LoggerFactory.getLogger(SecuritiesController.class);

    private SecurityEnrichmentService securityEnrichmentService;

    @Autowired
    public SecuritiesController(SecurityEnrichmentService securityEnrichmentService) {
        this.securityEnrichmentService = securityEnrichmentService;
    }

    @RequestMapping("/api/v1/isin/{isin}")
    public ResponseEntity<DataTransferObject> security(@PathVariable("isin") String isin) {
        try {
            Security security = securityEnrichmentService.enrich(new Security(isin));
            return ResponseEntity.ok(security);
        } catch (IORuntimeException | IOException e) {
            log.warn(e.getMessage(), e);
            return new ResponseEntity<>(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotYetImplementedException e) {
            log.warn(e.getMessage(), e);
            return new ResponseEntity<>(new Error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage()), HttpStatus.NOT_IMPLEMENTED);
        }
    }
}
