package pl.dev.news.openapitools;

import org.openapitools.codegen.languages.SpringCodegen;

public class CustomSpringCodegen extends SpringCodegen {

    public CustomSpringCodegen() {
        embeddedTemplateDir = templateDir = "JavaSpring";
    }

    @Override
    public String getName() {
        return "custom-spring";
    }

}
