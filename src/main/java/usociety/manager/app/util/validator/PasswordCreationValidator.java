package usociety.manager.app.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.Validate;

public class PasswordCreationValidator implements ConstraintValidator<PasswordCreationConstraint, String> {

    private static final String REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        try {
            Validate.matchesPattern(password, REGEX);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }

}
