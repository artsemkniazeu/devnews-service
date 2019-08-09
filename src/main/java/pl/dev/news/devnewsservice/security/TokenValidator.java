package pl.dev.news.devnewsservice.security;

public interface TokenValidator {

    boolean validateAccessToken(String accessToken);

    boolean validateRefreshToken(String refreshToken);
}
