package pl.dev.news.devnewsservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.BadCredentialsException;
import pl.dev.news.devnewsservice.exception.ConflictException;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.exception.UnauthorizedException;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.security.TokenProvider;
import pl.dev.news.devnewsservice.security.TokenValidator;
import pl.dev.news.devnewsservice.service.AuthService;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignInRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenResponse;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.incorrectPassword;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.refreshTokenInvalid;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithEmailDeleted;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithEmailNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdIsLocked;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdIsNotEnabled;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final QUserEntity qUserEntity = QUserEntity.userEntity;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final TokenValidator tokenValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    @Transactional
    public RestTokenResponse signIn(final RestSignInRequest restSignInRequest) {
        return signIn(restSignInRequest.getEmail(), restSignInRequest.getPassword());
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
    public RestTokenResponse signUp(final RestSignUpRequest restSignupRequest) {
        final UserEntity userEntity = userMapper.toEntity(restSignupRequest);
        userEntity.setRole(USER);
        userEntity.setPassword(passwordEncoder.encode(restSignupRequest.getPassword()));
        userRepository.saveAndFlush(userEntity);
        return signIn(restSignupRequest.getEmail(), restSignupRequest.getPassword());
    }

    private RestTokenResponse signIn(final String email, final String password) {
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
            throw new BadCredentialsException(userWithIdIsNotEnabled, userEntity.getId());
        }
        if (userEntity.isLocked()) {
            throw new BadCredentialsException(userWithIdIsLocked, userEntity.getId());
        }
        return tokenProvider.createTokenModel(userEntity);
    }

}
