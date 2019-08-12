package pl.dev.news.devnewsservice.controller;

import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.utils.PathUtils;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SwaggerControllerTest extends AbstractIntegrationTest {

    @Test
    public void testSwaggerUiRedirect() throws Exception {
        mockMvc.perform(
                get(PathUtils.generate("/"))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
    }

    @Test
    public void testSwaggerJson() throws Exception {
        mockMvc.perform(
                get(PathUtils.generate("/swagger.json"))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}
