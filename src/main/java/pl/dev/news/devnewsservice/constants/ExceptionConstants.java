package pl.dev.news.devnewsservice.constants;

public class ExceptionConstants {
    // NotFoundException
    public static String userWithIdNotFound = "User with id {} could not be found";
    public static String userWithEmailNotFound = "User with email {} could not be found";
    public static String tagWithIdNotFound = "Tag with id {} could not be found";
    public static String postWithIdNotFound = "Post with id {} could not be found";
    public static String groupWithIdNotFound = "Group with id {} could not be found";

    public static String categoryWithIdNotFound = "Category with id {} not be found";

    // ConflictException
    public static String userWithEmailDeleted = "Account with email {} was deleted!";

    // UnauthorizedException
    public static String incorrectPassword = "Incorrect password";
    public static String refreshTokenInvalid = "Invalid refresh token!";



}
