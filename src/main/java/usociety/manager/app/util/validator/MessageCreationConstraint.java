package usociety.manager.app.util.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = MessageCreationValidator.class)
@Target( { ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageCreationConstraint {

    String message() default "Si el mensaje es de tipo texto, el message.content es requerido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}


