package usociety.manager.domain.service.common;

import java.time.Clock;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.UserGroupRepository;

@Service
public abstract class CommonServiceImpl implements CommonService {

    @Autowired
    protected Clock clock;

    @Autowired
    private UserConnector userConnector;

    @Autowired
    private UserGroupRepository userGroupRepository;

    public CommonServiceImpl() {
        super();
    }

    protected void validateIfUserIsMember(String username, Long groupId, String sendingGroupMessageErrorCode)
            throws GenericException {
        UserDTO user = userConnector.get(username);
        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(groupId, user.getId());
        if (!optionalUserGroup.isPresent()) {
            throw new GenericException("The user is not member of the group.", sendingGroupMessageErrorCode);
        }
    }

}
