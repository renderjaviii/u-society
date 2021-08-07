package usociety.manager.app.util.validator;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

@SuppressWarnings( { "java:S5869", "java:S3011" })
public class AlphanumericValidator implements ConstraintValidator<AlphanumericConstraint, String> {

    private static final String FIELD_NAME_ATTRIBUTE = "basePath";
    public static final String ALPHANUMERIC_REGEX = "[\\w\\d\\s]+";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isNotEmpty(value) ? Pattern.matches(ALPHANUMERIC_REGEX, value) : FALSE;
    }

    /**
     * Function that allows to get the attribute's name dynamically using reflection
     *
     * @return Validated field's name
     */
    private Object getFieldName(ConstraintValidatorContext context)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = ((ConstraintValidatorContextImpl) context).getClass().getDeclaredField(FIELD_NAME_ATTRIBUTE);
        field.setAccessible(TRUE);
        return field.get(context);
    }

}
