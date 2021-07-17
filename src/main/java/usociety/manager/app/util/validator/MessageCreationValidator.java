package usociety.manager.app.util.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import usociety.manager.app.api.MessageApi;
import usociety.manager.domain.enums.MessageTypeEnum;

public class MessageCreationValidator implements ConstraintValidator<PostCreationConstraint, MessageApi> {

    @Override
    public boolean isValid(MessageApi message, ConstraintValidatorContext context) {
        if (MessageTypeEnum.TEXT == message.getType()) {
            return Objects.nonNull(message.getContent());
        }
        return true;
    }

}
