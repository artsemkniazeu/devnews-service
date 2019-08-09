package pl.dev.news.devnewsservice.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionConstants {
    // NotFoundException
    public static String userWithEmailNotExists = "User with same email does not exist";

    // ConflictException
    public static String userWithEmailDeleted = "Account with the same email was deleted!";

    // UnauthorizedException
    public static String incorrectPassword = "Incorrect password";
    public static String refreshTokenInvalid = "Invalid refresh token!";
}
