package pl.dev.news.devnewsservice.security.impl;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dev.news.devnewsservice.config.AppConfiguration;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.entity.UserRoleEntity;
import pl.dev.news.devnewsservice.security.TokenProvider;
import pl.dev.news.model.rest.RestTokenModel;
import pl.dev.news.model.rest.RestTokenResponse;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.io.Decoders.BASE64;

@Component
@RequiredArgsConstructor
public class TokenProviderImpl implements TokenProvider {

    private static final int MILLIS_IN_SECOND = 1000;
    private static final String TYPE_KEY = "typ";
    private static final String ROLE_KEY = "rol";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private final AppConfiguration appConfiguration;

    private Key key;
    private long accessTokenValidityMillils;
    private long refreshTokenValidityMillils;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(BASE64.decode(appConfiguration.getJwt().getBase64Secret()));
        accessTokenValidityMillils = appConfiguration.getJwt().getAccessTokenValidity() * MILLIS_IN_SECOND;
        refreshTokenValidityMillils = appConfiguration.getJwt().getRefreshTokenValidity() * MILLIS_IN_SECOND;
    }

    @Override
    public RestTokenResponse createTokenModel(final UserEntity userEntity) {

        final long currentTimeMillis = System.currentTimeMillis();
        final Date issuedAt = new Date(currentTimeMillis);

        final Date accessExpiresIn = new Date(currentTimeMillis + accessTokenValidityMillils);
        final Date refreshExpiresIn = new Date(currentTimeMillis + refreshTokenValidityMillils);

        final String accessToken = createToken(userEntity, issuedAt, accessExpiresIn, ACCESS_TOKEN);
        final String refreshToken = createToken(userEntity, issuedAt, refreshExpiresIn, REFRESH_TOKEN);

        final RestTokenModel refresh = new RestTokenModel()
                .issuedAt(issuedAt.getTime())
                .expiresIn(refreshExpiresIn.getTime())
                .token(refreshToken);

        final RestTokenModel access = new RestTokenModel()
                .issuedAt(issuedAt.getTime())
                .expiresIn(accessExpiresIn.getTime())
                .token(accessToken);

        return new RestTokenResponse()
                .access(access)
                .refresh(refresh);
    }

    @Override
    public RestTokenResponse refreshToken(final String refreshTokenOld) {

        final long currentTimeMillis = System.currentTimeMillis();
        final Date issuedAt = new Date(currentTimeMillis);

        final Date accessExpiresIn = new Date(currentTimeMillis + accessTokenValidityMillils);
        final Date refreshExpiresIn = new Date(currentTimeMillis + refreshTokenValidityMillils);

        final UUID userId = getUserIdByToken(refreshTokenOld);
        final UserRoleEntity role = getUserRoleByToken(refreshTokenOld);

        final String accessToken = createToken(userId, role, issuedAt, accessExpiresIn, ACCESS_TOKEN);
        final String refreshToken = createToken(userId, role, issuedAt, refreshExpiresIn, REFRESH_TOKEN);

        final RestTokenModel refresh = new RestTokenModel()
                .issuedAt(issuedAt.getTime())
                .expiresIn(refreshExpiresIn.getTime())
                .token(refreshToken);

        final RestTokenModel access = new RestTokenModel()
                .issuedAt(issuedAt.getTime())
                .expiresIn(accessExpiresIn.getTime())
                .token(accessToken);

        return new RestTokenResponse()
                .access(access)
                .refresh(refresh);
    }

    @Override
    public UserEntity buildUserEntityByToken(final String token) {
        final UserEntity userEntity = new UserEntity();
        userEntity.setId(getUserIdByToken(token));
        userEntity.setRole(getUserRoleByToken(token));
        return userEntity;
    }

    private String createToken(
            final UserEntity userEntity,
            final Date issuedAt,
            final Date expiresIn,
            final String tokenType
    ) {
        return createToken(userEntity.getId(), userEntity.getRole(), issuedAt, expiresIn, tokenType);
    }

    private String createToken(
            final UUID userId,
            final UserRoleEntity userRole,
            final Date issuedAt,
            final Date expiresIn,
            final String tokenType
    ) {
        final JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setSubject(userId.toString());
        jwtBuilder.setIssuedAt(issuedAt);
        jwtBuilder.setExpiration(expiresIn);
        jwtBuilder.claim(TYPE_KEY, tokenType);
        jwtBuilder.claim(ROLE_KEY, userRole.toString());
        jwtBuilder.signWith(key, SignatureAlgorithm.HS512);
        return jwtBuilder.compact();
    }

    private UUID getUserIdByToken(final String token) {
        return UUID.fromString(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject());
    }

    private UserRoleEntity getUserRoleByToken(final String token) {
        return UserRoleEntity.fromValue(
                (String) Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().get(ROLE_KEY)
        );
    }

}
