package pl.dev.news.devnewsservice.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$";

    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    @Override
    public void initialize(final Phone phone) {

    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return PHONE_PATTERN.matcher(value).matches();
    }
}
