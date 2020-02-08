package pl.dev.news.devnewsservice.constants;

public class ExceptionConstants {
    // NotFoundException
    public static String userWithIdNotFound = "User with id {} could not be found";
    public static String userWithEmailNotFound = "User with email {} could not be found";
    public static String userWithActivationKeyNotFound = "User with activation key {} could not be found";
    public static String tagWithIdNotFound = "Tag with id {} could not be found";
    public static String postWithIdNotFound = "Post with id {} could not be found";
    public static String groupWithIdNotFound = "Group with id {} could not be found";
    public static String categoryWithIdNotFound = "Category with id {} not be found";
    public static String commentWithIdNotFound = "Comment with id {} not be found";

    // ConflictException
    public static String userWithEmailDeleted = "Account with email {} was deleted!";
    public static String userWithEmailAlreadyExists = "User with email {} is already exists";
    public static String groupWithValueAlreadyExists = "Group with value {} is already exists";

    // UnauthorizedException
    public static String incorrectPassword = "Incorrect password";
    public static String refreshTokenInvalid = "Invalid refresh token!";

    // BadCredentialsException
    public static String userWithIdIsLocked = "User with id {} is locked";
    public static String userWithIdIsNotEnabled = "User with id {} is not enabled";

    // BadRequestException
    public static String invalidImageFormat = "Invalid image format";
    public static String userIsAlreadyActivated = "User with email {} is already activated";
    public static String verificationCodeNotValidForPhone = "Verification code {} is not valid for phone number {}";

    // UnprocessableEntityException
    public static String fileCorruptOrUnreadable = "Can't read file, file is corrupt and unreadable";




}
