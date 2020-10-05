package usociety.manager.app.util.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import usociety.manager.app.api.PostApi;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

public class PostCreationValidator implements ConstraintValidator<PostCreationConstraint, PostApi> {

    @Override
    public boolean isValid(PostApi post, ConstraintValidatorContext context) {
        PostAdditionalData content = post.getContent();
        if (PostTypeEnum.SURVEY == content.getType()) {
            return Objects.nonNull(content.getOptions())
                    && Objects.nonNull(post.getContent().getValue())
                    && Objects.isNull(post.getContent().getDescription())
                    && !content.getOptions().isEmpty()
                    && Objects.nonNull(post.getExpirationDate())
                    && !post.isPublic();
        }
        if (PostTypeEnum.TEXT == content.getType()) {
            return Objects.nonNull(post.getContent().getValue())
                    && Objects.isNull(post.getContent().getDescription());
        }
        return true;
    }

}
