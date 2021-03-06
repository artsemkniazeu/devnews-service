package pl.dev.news.devnewsservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.ConflictException;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.exception.UnauthorizedException;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.security.TokenProvider;
import pl.dev.news.devnewsservice.security.TokenValidator;
import pl.dev.news.devnewsservice.service.AuthService;
import pl.dev.news.devnewsservice.service.MailService;
import pl.dev.news.devnewsservice.service.TransactionTemplate;
import pl.dev.news.devnewsservice.utils.SerializationUtils;
import pl.dev.news.devnewsservice.validator.Email;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignInRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.incorrectPassword;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.refreshTokenInvalid;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithActivationKeyNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithEmailAlreadyExists;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithEmailDeleted;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithEmailNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdIsLocked;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdIsNotEnabled;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final TokenProvider tokenProvider;

    private final TokenValidator tokenValidator;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    private final TransactionTemplate transactionTemplate;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;

    @Override
    @Transactional
    public RestTokenResponse signIn(final RestSignInRequest restSignInRequest) {
        final String email = restSignInRequest.getEmail();
        final String password = restSignInRequest.getPassword();
        final UserEntity userEntity = userRepository
                .findOne(qUserEntity.email.eq(email))
                .orElseThrow(() -> new NotFoundException(userWithEmailNotFound, email));

        if (userEntity.getDeletedAt() != null) {
            throw new ConflictException(userWithEmailDeleted, email);
        }
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new UnauthorizedException(incorrectPassword);
        }
        if (!userEntity.isEnabled()) {
            throw new UnauthorizedException(userWithIdIsNotEnabled, userEntity.getId());
        }
        if (userEntity.isLocked()) {
            throw new UnauthorizedException(userWithIdIsLocked, userEntity.getId());
        }
        return tokenProvider.createTokenModel(userEntity);
    }

    @Override
    @Transactional
    public RestTokenResponse refreshToken(final RestRefreshTokenRequest restRefreshTokenRequest) {
        if (!tokenValidator.validateRefreshToken(restRefreshTokenRequest.getRefreshToken())) {
            throw new UnauthorizedException(refreshTokenInvalid);
        }
        return tokenProvider.refreshToken(restRefreshTokenRequest.getRefreshToken());
    }

    @Override
    @Transactional
    public void signUp(final RestSignUpRequest restSignupRequest) {
        if (userRepository.exists(qUserEntity.email.eq(restSignupRequest.getEmail()))) {
            throw new ConflictException(userWithEmailAlreadyExists, restSignupRequest.getEmail());
        }
        final UserEntity userEntity = userMapper.toEntity(restSignupRequest);
        userEntity.setRole(USER);
        userEntity.setPassword(passwordEncoder.encode(restSignupRequest.getPassword()));
        userEntity.setActivationKey(UUID.randomUUID());
        final UserEntity saved = userRepository.saveAndFlush(userEntity);
        transactionTemplate.afterCommit(() -> mailService.sendEmailActivationCode(saved));
    }

    @Override
    @Transactional
    public void activate(final UUID key) {
        final UserEntity userEntity = userRepository
                .findOne(qUserEntity.activationKey.eq(key))
                .orElseThrow(() -> new NotFoundException(userWithActivationKeyNotFound, key));
        userEntity.setEnabled(true);
        userEntity.setActivationKey(null);
        final UserEntity saved = userRepository.saveAndFlush(userEntity);
        transactionTemplate.afterCommit(() -> mailService.sendWelcomeEmail(saved));
    }

    @Override
    @Transactional
    public void emailActivate(final String key) {
        final HashMap<String, String> map = (HashMap<String, String>) SerializationUtils
                .deserialize(URLDecoder.decode(key, StandardCharsets.UTF_8));
        @Email
        final String email = map.get("email");
        final UUID activationKey = UUID.fromString(map.get("key"));
        final UserEntity userEntity = userRepository
                .findOne(qUserEntity.activationKey.eq(activationKey))
                .orElseThrow(() -> new NotFoundException(userWithActivationKeyNotFound, key));
        userEntity.setActivationKey(null);
        userEntity.setEmail(email);
        userRepository.saveAndFlush(userEntity);
    }

}
