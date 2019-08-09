package pl.dev.news.devnewsservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SwaggerController {

    private final String json;

    @SneakyThrows
    public SwaggerController(final ResourceLoader resourceLoader,
                             final ObjectMapper objectMapper,
                             final Yaml yaml) {
        final Map<?, ?> swaggerYaml = yaml
                .load(resourceLoader.getResource("classpath:/swagger/openapi.yaml")
                        .getInputStream());
        this.json = objectMapper.writeValueAsString(swaggerYaml);
    }

    @GetMapping("/")
    public String swaggerUiRedirect() {
        return "redirect:swagger-ui.html";
    }

    @ResponseBody
    @GetMapping("/swagger.json")
    public String swaggerJson() {
        return json;
    }

}
