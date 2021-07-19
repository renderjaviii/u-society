package usociety.manager.domain.service.react.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.React;
import usociety.manager.domain.repository.ReactRepository;
import usociety.manager.domain.service.common.impl.AbstractServiceImpl;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.react.ReactService;

@Service
public class ReactServiceImpl extends AbstractServiceImpl implements ReactService {

    private static final String REACTING_IN_POST_ERROR_CODE = "ERROR_REACTING_IN_POST";

    private final ReactRepository reactRepository;

    @Autowired
    public ReactServiceImpl(ReactRepository reactRepository) {
        this.reactRepository = reactRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void create(String username, Post post, ReactTypeEnum value) throws GenericException {
        UserApi user = getUser(username);
        validatePostType(post);

        Optional<React> optionalReact = reactRepository.findAllByPostIdAndUserId(post.getId(), user.getId());
        if (optionalReact.isPresent()) {
            React savedReact = optionalReact.get();
            savedReact.setValue(value.getCode());
            reactRepository.save(savedReact);
        } else {
            reactRepository.save(React.newBuilder()
                    .value(value.getCode())
                    .userId(user.getId())
                    .post(post)
                    .build());
        }
    }

    private void validatePostType(Post post) throws GenericException {
        try {
            PostAdditionalData postAdditionalData = objectMapper.readValue(post.getContent(), PostAdditionalData.class);
            if (PostTypeEnum.SURVEY == postAdditionalData.getType()) {
                throw new GenericException("Reaction not allowed", REACTING_IN_POST_ERROR_CODE);
            }
        } catch (JsonProcessingException e) {
            throw new GenericException("Corrupt post's information.", REACTING_IN_POST_ERROR_CODE);
        }
    }

}
