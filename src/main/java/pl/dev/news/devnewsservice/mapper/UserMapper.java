package pl.dev.news.devnewsservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestUserModel;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "socials", ignore = true)
    RestUserModel toModel(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "profileBgUrl", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "timezone", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    UserEntity toEntityFromRestSignupRequest(RestSignUpRequest restSignupRequest);
}
