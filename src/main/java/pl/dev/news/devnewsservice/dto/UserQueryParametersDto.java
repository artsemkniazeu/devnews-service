package pl.dev.news.devnewsservice.dto;

import lombok.Getter;
import lombok.Setter;
import pl.dev.news.devnewsservice.utils.CommonUtils;
import pl.dev.news.model.rest.RestUserQueryParameters;

@Getter
@Setter
public class UserQueryParametersDto {

    private String id;
    private String email;
    private String name;
    private String username;

    public UserQueryParametersDto(final RestUserQueryParameters parameters) {
        this.id = CommonUtils.nullSafeToString(parameters.getId());
        this.email = CommonUtils.nullSafeToString(parameters.getEmail());
        this.name = CommonUtils.nullSafeToString(parameters.getName());
        this.username = CommonUtils.nullSafeToString(parameters.getUsername());
    }
}
