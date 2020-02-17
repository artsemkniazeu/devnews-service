package pl.dev.news.devnewsservice.service.impl;

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
import pl.dev.news.devnewsservice.exception.ConflictException;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.TwilioMapper;
import pl.dev.news.devnewsservice.mapper.UploadMapper;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.security.SecurityResolver;
import pl.dev.news.devnewsservice.service.MailService;
import pl.dev.news.devnewsservice.service.TransactionTemplate;
import pl.dev.news.devnewsservice.service.TwilioService;
import pl.dev.news.devnewsservice.service.UploadService;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.devnewsservice.utils.CommonUtils;
import pl.dev.news.devnewsservice.utils.ImageUtils;
import pl.dev.news.devnewsservice.utils.SerializationUtils;
import pl.dev.news.model.rest.RestEmailModel;
import pl.dev.news.model.rest.RestPhoneModel;
import pl.dev.news.model.rest.RestPhoneResponseModel;
import pl.dev.news.model.rest.RestUploadModel;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

import java.util.HashMap;
import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.invalidImageFormat;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userIsAlreadyActivated;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithEmailAlreadyExists;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithEmailNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.verificationCodeNotValidForPhone;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppConfiguration appConfiguration;

    private final SecurityResolver securityResolver;

    private final UserRepository userRepository;

    private final TwilioService twilioService;

    private final UploadService uploadService;

    private final MailService mailService;

    private final TransactionTemplate transactionTemplate;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final TwilioMapper twilioMapper = TwilioMapper.INSTANCE;

    private final UploadMapper uploadMapper = UploadMapper.INSTANCE;

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
        return userRepository.findAll(
                CommonUtils.nullSafeToString(parameters.getEmail()),
                CommonUtils.nullSafeToString(parameters.getName()),
                CommonUtils.nullSafeToString(parameters.getUsername()),
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
    @Transactional
    public RestUploadModel uploadImage(final UUID userId, final MultipartFile file) {
        final UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        final UploadEntity saved = uploadImage(entity, entity.getImage(), file);
        userMapper.updateImage(entity, saved);
        userRepository.saveAndFlush(entity);
        return uploadMapper.toModel(saved);
    }

    @Override
    @Transactional
    public RestUploadModel uploadBackground(final UUID userId, final MultipartFile file) {
        final UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        final UploadEntity saved = uploadImage(entity, entity.getBg(), file);
        userMapper.updateBackground(entity, saved);
        userRepository.saveAndFlush(entity);
        return uploadMapper.toModel(saved);
    }

    @Override
    @Transactional
    public RestPhoneResponseModel sendVerificationCode(final UUID userId, final RestPhoneModel restPhoneModel) {
        final Verification verification = twilioService.sendVerificationSms(restPhoneModel.getPhone());
        return twilioMapper.toModel(verification);
    }

    @Override
    @Transactional
    public RestPhoneResponseModel verifyPhoneNumber(final UUID userId, final RestPhoneModel restPhoneModel) {
        final UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        final VerificationCheck verificationCheck = twilioService
                .checkVerificationCode(restPhoneModel.getPhone(), restPhoneModel.getCode());
        if (!verificationCheck.getValid()) {
            throw new BadRequestException(
                    verificationCodeNotValidForPhone,
                    restPhoneModel.getCode(),
                    restPhoneModel.getPhone()
            );
        }
        entity.setPhone(restPhoneModel.getPhone());
        userRepository.saveAndFlush(entity);
        return twilioMapper.toModel(verificationCheck);
    }

    @Override
    @Transactional
    public void resendActivationCode(final String email) {
        final UserEntity userEntity = userRepository
                .findOne(userRepository.soft(qUserEntity.email.eq(email)))
                .orElseThrow(() -> new NotFoundException(userWithEmailNotFound, email));
        if (userEntity.isEnabled()) {
            throw new BadRequestException(userIsAlreadyActivated, email);
        }
        transactionTemplate.afterCommit(() -> mailService.sendEmailActivationCode(userEntity));
    }

    @Override
    @Transactional
    public void changeEmailAddress(final UUID userId, final RestEmailModel restEmailModel) {
        final UserEntity userEntity = userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        if (userRepository.exists(qUserEntity.email.eq(restEmailModel.getEmail()))) {
            throw new ConflictException(userWithEmailAlreadyExists, restEmailModel.getEmail());
        }
        final UUID activationKey = UUID.randomUUID();
        final HashMap<String, String> map = new HashMap<>();
        map.put("email", restEmailModel.getEmail());
        map.put("key", activationKey.toString());
        userEntity.setActivationKey(activationKey);
        final UserEntity saved = userRepository.saveAndFlush(userEntity);
        transactionTemplate.afterCommit(() -> mailService
                .sendChangeEmailActivationCode(saved, SerializationUtils.serialize(map)));
    }

    @Override
    @Transactional
    public void follow(final UUID userId) {
        final UserEntity follower = securityResolver.getUser();
        final UserEntity userEntity = userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        userEntity.addFollower(follower);
        userRepository.saveAndFlush(userEntity);
    }

    @Override
    @Transactional
    public void unFollow(final UUID userId) {
        final UserEntity follower = securityResolver.getUser();
        final UserEntity userEntity = userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        userEntity.removeFollower(follower);
        userRepository.saveAndFlush(follower);
    }

    private UploadEntity uploadImage(final UserEntity entity, final UploadEntity old, final MultipartFile file) {
        if (!ImageUtils.isValid(file)) {
            throw new BadRequestException(invalidImageFormat);
        }
        if (old != null) {
            uploadService.delete(old);
        }
        return uploadService.upload(entity, file, appConfiguration.getGoogle().getStorage().getImageBucket());
    }
}
