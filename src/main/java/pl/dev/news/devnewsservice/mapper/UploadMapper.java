package pl.dev.news.devnewsservice.mapper;

import com.google.cloud.storage.BlobInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestUploadModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UploadMapper {

    UploadMapper INSTANCE = Mappers.getMapper(UploadMapper.class);

    UploadEntity toEntity(RestUploadModel model);

    RestUploadModel toModel(UploadEntity entity);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "blobInfo.blobId.bucket", target = "bucket")
    @Mapping(source = "blobInfo.blobId.name", target = "filename")
    @Mapping(source = "blobInfo.blobId.generation", target = "generation")
    @Mapping(source = "blobInfo.mediaLink", target = "url")
    UploadEntity toEntity(UserEntity user, BlobInfo blobInfo);

}
