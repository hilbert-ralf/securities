package de.hilbert.securities.controller;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ralf Hilbert
 * @since 18.01.2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecuritiesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "GUESTS")
    public void testStockRequest() throws Exception {
        mockMvc.perform(get("/api/v1/isin/DE0005552004"))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString("earningsPerStockAndYearAfterTax")))
                .andExpect(content().string(CoreMatchers.containsString("grahamPER")))
                .andExpect(content().string(CoreMatchers.containsString("DE0005552004")));
    }

    @Test
    @WithMockUser(roles = "GUESTS")
    public void testETFRequest() throws Exception {
        mockMvc.perform(get("/api/v1/isin/US4642863926"))
                .andExpect(status().is(501))
                .andExpect(content().string(CoreMatchers.containsString("Securities of this type are not yet supported")));
    }

    @Test
    public void testRequestForMissingAuth() throws Exception {
        mockMvc.perform(get("/api/v1/isin/anyIsin"))
                .andExpect(status().is(401));
    }
}