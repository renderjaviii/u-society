package usociety.manager.domain.service.common.impl;

import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.common.AbstractDelegate;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;

@Primary
@Component
public class AbstractDelegateImpl implements AbstractDelegate {

    @Autowired
    protected Clock clock;

    @Autowired
    protected UserService userService;

    @Autowired
    protected CustomObjectMapper objectMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    protected AbstractDelegateImpl() {
        super();
    }

    @Override
    public Group getGroup(Long id) throws GenericException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GenericException("Group does not exist", GROUP_NOT_FOUND));
    }

    @Override
    public Optional<UserGroup> getUserGroup(UserApi user, Long groupId, List<UserGroupStatusEnum> statuses) {
        if (!CollectionUtils.isEmpty(statuses)) {
            List<Integer> statusesCodes = statuses
                    .stream()
                    .map(UserGroupStatusEnum::getCode)
                    .collect(Collectors.toList());
            return userGroupRepository.findByGroupIdAndUserIdAndStatusIn(groupId, user.getId(), statusesCodes);

        }
        return userGroupRepository.findByGroupIdAndUserId(groupId, user.getId());
    }

}
