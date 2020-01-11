package pl.dev.news.devnewsservice.dto;

import lombok.Getter;
import lombok.Setter;
import pl.dev.news.devnewsservice.utils.CommonUtils;
import pl.dev.news.model.rest.RestGroupQueryParameters;

@Getter
@Setter
public class GroupQueryParametersDto {

    private String name;

    private String value;

    private String ownerId;

    private String userId;

    public GroupQueryParametersDto(final RestGroupQueryParameters parameters) {
        this.name = CommonUtils.nullSafeToString(parameters.getName());
        this.value = CommonUtils.nullSafeToString(parameters.getValue());
        this.ownerId = CommonUtils.nullSafeToString(parameters.getOwnerId());
        this.userId = CommonUtils.nullSafeToString(parameters.getUserId());
    }
}
