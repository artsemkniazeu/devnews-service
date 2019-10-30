package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.model.rest.RestUploadModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UploadMapper {

    UploadMapper INSTANCE = Mappers.getMapper(UploadMapper.class);

    UploadEntity toEntity(RestUploadModel model);

    RestUploadModel toModel(UploadEntity entity);

}
