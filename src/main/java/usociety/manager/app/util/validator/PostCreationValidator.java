package usociety.manager.app.util.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import usociety.manager.app.api.PostApi;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

public class PostCreationValidator implements ConstraintValidator<PostCreationConstraint, PostApi> {

    private static final String SURVEY_ERROR_MESSAGE = "Para encuesta, la fecha de expiración, el content.value y  las content.options son obligatorios.";
    private static final String TEXT_ERROR_MESSAGE = "Si el post es de tipo texto o encuesta, el content.value es obligatorio.";
    private static final String IMAGE_ERROR_MESSAGE = "La descripción es obligatoria para posts de tipo imagen.";

    @Override
    public boolean isValid(PostApi post, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        PostAdditionalData content = post.getContent();
        if (PostTypeEnum.SURVEY == content.getType()) {
            context.buildConstraintViolationWithTemplate(SURVEY_ERROR_MESSAGE).addConstraintViolation();
            return Objects.nonNull(content.getOptions())
                    && Objects.nonNull(post.getContent().getValue())
                    && !content.getOptions().isEmpty()
                    && Objects.nonNull(post.getExpirationDate());
        }

        if (PostTypeEnum.TEXT == content.getType()) {
            context.buildConstraintViolationWithTemplate(TEXT_ERROR_MESSAGE).addConstraintViolation();
            return Objects.nonNull(post.getContent().getValue());
        }

        if (PostTypeEnum.IMAGE == content.getType() && Objects.isNull(post.getDescription())) {
            context.buildConstraintViolationWithTemplate(IMAGE_ERROR_MESSAGE).addConstraintViolation();
            return false;
        }
        return true;
    }

}
