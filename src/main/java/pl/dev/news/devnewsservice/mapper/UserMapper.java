package pl.dev.news.devnewsservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestUserModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    RestUserModel toModel(UserEntity userEntity);

    UserEntity toEntity(RestSignUpRequest restSignupRequest);

}
