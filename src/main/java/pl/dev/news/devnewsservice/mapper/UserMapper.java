package pl.dev.news.devnewsservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestUserModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        TagMapper.class,
        PostMapper.class
})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    RestUserModel toModel(UserEntity userEntity);

    UserEntity toEntity(RestSignUpRequest restSignupRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "phone", ignore = true)
    //@Mapping(target = "username", ignore = true) // TODO username validation
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "bgUrl", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserEntity update(@MappingTarget UserEntity userEntity, RestUserModel restUserModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "uploadEntity", target = "image")
    @Mapping(source = "uploadEntity.url", target = "imageUrl")
    void updateImage(@MappingTarget UserEntity entity, UploadEntity uploadEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "uploadEntity", target = "bg")
    @Mapping(source = "uploadEntity.url", target = "bgUrl")
    void updateBackground(@MappingTarget UserEntity entity, UploadEntity uploadEntity);
}
