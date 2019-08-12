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

    RestUserModel toModel(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "bgUrl", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "followingTags", ignore = true)
    @Mapping(target = "bookmarks", ignore = true)
    @Mapping(target = "followingUsers", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "uploads", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    UserEntity toEntity(RestSignUpRequest restSignupRequest);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "followingTags", ignore = true)
    @Mapping(target = "bookmarks", ignore = true)
    @Mapping(target = "followingUsers", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "uploads", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    UserEntity toEntity(RestUserModel restUserModel);
}
