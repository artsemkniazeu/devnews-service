package pl.dev.news.devnewsservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
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
    @Mapping(target = "username", ignore = true)
    UserEntity update(@MappingTarget UserEntity userEntity, RestUserModel restUserModel);

}
