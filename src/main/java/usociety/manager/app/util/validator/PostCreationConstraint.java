package usociety.manager.app.util.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PostCreationValidator.class)
@Target( { ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PostCreationConstraint {

    String message() default "If post type is survey the options and the expirationDate are mandatory and must be private.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}


