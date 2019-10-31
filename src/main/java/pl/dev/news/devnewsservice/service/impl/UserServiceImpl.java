package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.devnewsservice.config.AppConfiguration;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.BadRequestException;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.TwilioMapper;
import pl.dev.news.devnewsservice.mapper.UploadMapper;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.UploadRepository;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.service.GoogleFileService;
import pl.dev.news.devnewsservice.service.MailService;
import pl.dev.news.devnewsservice.service.TransactionTemplate;
import pl.dev.news.devnewsservice.service.TwilioService;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.devnewsservice.utils.ImageUtils;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestPhoneModel;
import pl.dev.news.model.rest.RestPhoneResponseModel;
import pl.dev.news.model.rest.RestUploadModel;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.invalidImageFormat;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdNotFound;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppConfiguration appConfiguration;

    private final UserRepository userRepository;

    private final UploadRepository uploadRepository;

    private final GoogleFileService fileService;

    private final MailService mailService;

    private final TwilioService twilioService;

    private final TransactionTemplate transactionTemplate;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final UploadMapper uploadMapper = UploadMapper.INSTANCE;

    private final TwilioMapper twilioMapper = TwilioMapper.INSTANCE;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;


    @Override
    @Transactional
    public void delete(final UUID userId) {
        if (!userRepository.softExistsById(userId)) {
            throw new NotFoundException(userWithIdNotFound, userId);
        }
        userRepository.softDeleteById(userId);
    }

    @Override
    @Transactional
    public RestUserModel get(final UUID userId) {
        final UserEntity userEntity = userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        return UserMapper.INSTANCE.toModel(userEntity);
    }

    @Override
    @Transactional
    public Page<RestUserModel> getUsers(
            final RestUserQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .andLikeAny(parameters.getName(), qUserEntity.firstName, qUserEntity.lastName)
                .andLikeAny(parameters.getUsername(), qUserEntity.username)
                .andLikeAny(parameters.getEmail(), qUserEntity.email)
                .build();
        return userRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(userMapper::toModel);
    }

    @Override
    @Transactional
    public RestUserModel update(final UUID userId, final RestUserModel restUserModel) {
        final UserEntity entity = userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        userMapper.update(entity, restUserModel);
        final UserEntity saved = userRepository.saveAndFlush(entity);
        return userMapper.toModel(saved);
    }

    @Override
    public RestUploadModel uploadImage(final UUID userId, final MultipartFile file) {
        if (!ImageUtils.isValid(file)) {
            throw new BadRequestException(invalidImageFormat);
        }
        final UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        final String url = fileService.bucketUpload(file, appConfiguration.getGoogle().getStorage().getImageBucket());
        final UploadEntity uploadEntity = new UploadEntity();
        uploadEntity.setUrl(url);
        uploadEntity.setUser(entity);
        entity.setImageUrl(url);
        userRepository.saveAndFlush(entity);
        uploadRepository.saveAndFlush(uploadEntity);
        return uploadMapper.toModel(uploadEntity);
    }

    @Override
    public RestPhoneResponseModel verifyPhoneNumber(final UUID userId, final RestPhoneModel restPhoneModel) {
        final Verification verification = twilioService.sendVerificationSms(restPhoneModel.getPhone());
        return twilioMapper.toModel(verification);
    }

    @Override
    public RestPhoneResponseModel checkPhoneNumber(final UUID userId, final RestPhoneModel restPhoneModel) {
        final VerificationCheck verificationCheck = twilioService
                .checkVerificationCode(restPhoneModel.getPhone(), restPhoneModel.getCode());
        return twilioMapper.toModel(verificationCheck);
    }

}
