package pl.dev.news.devnewsservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SwaggerController {

    private final String uiConfiguration;
    private final String configuration;
    private final String json;

    @SneakyThrows
    public SwaggerController(final ResourceLoader resourceLoader,
                             final ObjectMapper objectMapper,
                             final Yaml yaml) {
        final JsonNode configurationUiJson = loadJson(
                objectMapper,
                resourceLoader,
                "swagger/configuration-ui.json"
        );
        final JsonNode configurationJson = loadJson(
                objectMapper,
                resourceLoader,
                "swagger/configuration.json"
        );
        final Map<?, ?> swaggerYaml = yaml
                .load(resourceLoader.getResource("classpath:/swagger/openapi.yaml")
                        .getInputStream());
        this.uiConfiguration = objectMapper.writeValueAsString(configurationUiJson);
        this.configuration = objectMapper.writeValueAsString(configurationJson);
        this.json = objectMapper.writeValueAsString(swaggerYaml);
    }

    @GetMapping("/")
    public String swaggerUiRedirect() {
        return "redirect:swagger-ui.html";
    }

    @ResponseBody
    @GetMapping("/swagger-resources/configuration/ui")
    public String swaggerUiConfiguration() {
        return uiConfiguration;
    }

    @ResponseBody
    @GetMapping("/swagger-resources")
    public String swaggerConfiguration() {
        return configuration;
    }

    @ResponseBody
    @GetMapping("/swagger.json")
    public String swaggerJson() {
        return json;
    }

    private JsonNode loadJson(
            final ObjectMapper objectMapper,
            final ResourceLoader resourceLoader,
            final String location
    ) throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(location)) {
            return objectMapper.readTree(inputStream);
        }
    }

}
