package usociety.manager.app.util.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

public class PostCreationValidator implements ConstraintValidator<PostCreationConstraint, CreatePostRequest> {

    private static final String SURVEY_ERROR_MESSAGE = "Para encuesta, la fecha de expiración, el content.value y  las content.options son obligatorios.";
    private static final String TEXT_ERROR_MESSAGE = "Si el post es de tipo texto o encuesta, el content.value es obligatorio.";
    private static final String IMAGE_ERROR_MESSAGE = "La descripción es obligatoria para posts de tipo imagen.";

    @Override
    public boolean isValid(CreatePostRequest request, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        PostAdditionalData content = request.getContent();
        if (PostTypeEnum.SURVEY == content.getType()) {
            context.buildConstraintViolationWithTemplate(SURVEY_ERROR_MESSAGE).addConstraintViolation();
            return Objects.nonNull(content.getOptions())
                    && Objects.nonNull(request.getContent().getValue())
                    && !content.getOptions().isEmpty()
                    && Objects.nonNull(request.getExpirationDate());
        }

        if (PostTypeEnum.TEXT == content.getType()) {
            context.buildConstraintViolationWithTemplate(TEXT_ERROR_MESSAGE).addConstraintViolation();
            return Objects.nonNull(request.getContent().getValue());
        }

        if (PostTypeEnum.IMAGE == content.getType() && Objects.isNull(request.getDescription())) {
            context.buildConstraintViolationWithTemplate(IMAGE_ERROR_MESSAGE).addConstraintViolation();
            return false;
        }
        return true;
    }

}
