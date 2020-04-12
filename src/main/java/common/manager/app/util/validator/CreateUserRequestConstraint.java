package common.manager.app.util.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = CreateUserRequestValidator.class)
@Target( { ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateUserRequestConstraint {

    String message() default "Comment in case validation faliled.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

