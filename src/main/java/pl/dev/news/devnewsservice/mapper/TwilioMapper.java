package pl.dev.news.devnewsservice.mapper;

import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.type.PhoneNumber;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.model.rest.RestPhoneResponseModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TwilioMapper {

    TwilioMapper INSTANCE = Mappers.getMapper(TwilioMapper.class);

    RestPhoneResponseModel toModel(Verification verification);

    @Mapping(source = "to", target = "phone")
    RestPhoneResponseModel toModel(VerificationCheck verificationCheck);

    @AfterMapping
    default void after(
            @MappingTarget final RestPhoneResponseModel model,
            final Verification verification
    ) {
        setPhone(model, verification.getTo());
    }


    default void setPhone(final RestPhoneResponseModel model, final PhoneNumber phoneNumber) {
        model.setPhone(phoneNumber.getEndpoint());
    }

}
