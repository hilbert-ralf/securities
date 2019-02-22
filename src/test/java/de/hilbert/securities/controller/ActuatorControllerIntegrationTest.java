package de.hilbert.securities.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ralf Hilbert
 * @since 22.02.2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ActuatorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMetricsRequestForMissingAuth() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().is(401));
    }

    @Test
    @WithMockUser(roles = "GUESTS")
    public void testMetricsRequestForWrongAuth() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(roles = "ADMINS")
    public void testMetricsRequestForCorrectAuth() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().is(200));
    }
}