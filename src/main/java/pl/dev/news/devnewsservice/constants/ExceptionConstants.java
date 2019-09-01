package pl.dev.news.devnewsservice.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionConstants {
    // NotFoundException
    public static String userWithIdNotFound = "User could not be found";
    public static String userWithEmailNotFound = "User with same email does not found";
    public static String tagWithIdNotFound = "Tag with requested id does not found";
    public static String postWithIdNotFound = "Post with requested id does not found";

    public static String categoryNotFound = "Category could not be found";
    public static String categoryWithIdNotFound = "Category with requested id does not found";

    // ConflictException
    public static String userWithEmailDeleted = "Account with the same email was deleted!";

    // UnauthorizedException
    public static String incorrectPassword = "Incorrect password";
    public static String refreshTokenInvalid = "Invalid refresh token!";
}
