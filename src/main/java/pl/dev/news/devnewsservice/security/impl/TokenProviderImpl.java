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
        this.key = Keys.hmacShaKeyFor(BASE64.decode(appConfiguration.getBase64Secret()));
        accessTokenValidityMillils = appConfiguration.getAccessTokenValidity() * MILLIS_IN_SECOND;
        refreshTokenValidityMillils = appConfiguration.getRefreshTokenValidity() * MILLIS_IN_SECOND;
    }

    @Override
    public RestTokenModel createTokenModel(final UserEntity userEntity) {

        final long currentTimeMillils = System.currentTimeMillis();
        final Date issuedAt = new Date(currentTimeMillils);

        final Date accessExpiresIn = new Date(currentTimeMillils + accessTokenValidityMillils);
        final Date refreshExpiresIn = new Date(currentTimeMillils + refreshTokenValidityMillils);

        final String accessToken = createToken(userEntity, issuedAt, accessExpiresIn, ACCESS_TOKEN);
        final String refreshToken = createToken(userEntity, issuedAt, refreshExpiresIn, REFRESH_TOKEN);

        return new RestTokenModel()
                .accessToken(accessToken)
                .accessIssuedAt(issuedAt.getTime())
                .accessExpiresIn(accessExpiresIn.getTime())
                .refreshToken(refreshToken)
                .refreshIssuedAt(issuedAt.getTime())
                .refreshExpiresIn(refreshExpiresIn.getTime());
    }

    @Override
    public RestTokenModel refreshToken(final String refreshToken) {

        final long currentTimeMillis = System.currentTimeMillis();
        final Date accessIssuedAt = new Date(currentTimeMillis);
        final Date refreshIssuedAt = Jwts.parser().setSigningKey(key)
                .parseClaimsJws(refreshToken).getBody().getIssuedAt();

        final Date accessExpiresIn = new Date(currentTimeMillis + accessTokenValidityMillils);
        final Date refreshExpiresIn = Jwts.parser().setSigningKey(key)
                .parseClaimsJws(refreshToken).getBody().getExpiration();

        final String accessToken = createToken(getUserIdByToken(refreshToken), getUserRoleByToken(refreshToken),
                accessIssuedAt, accessExpiresIn, ACCESS_TOKEN);

        return new RestTokenModel()
                .accessToken(accessToken)
                .accessIssuedAt(accessIssuedAt.getTime())
                .accessExpiresIn(accessExpiresIn.getTime())
                .refreshToken(refreshToken)
                .refreshIssuedAt(refreshIssuedAt.getTime())
                .refreshExpiresIn(refreshExpiresIn.getTime());
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
