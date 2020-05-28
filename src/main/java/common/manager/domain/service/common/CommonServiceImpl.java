package common.manager.domain.service.common;

import org.springframework.stereotype.Service;

@Service
public abstract class CommonServiceImpl implements CommonService {

    private static final String USER_NOT_FOUND_FORMAT = "Username: %s not found.";

    public CommonServiceImpl() {
        super();
    }

/*    public User getUser(Long userId) {
        return userRepository.getOne(userId);
    }

    public User getUser(String username) throws GenericException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GenericException(String.format(USER_NOT_FOUND_FORMAT, username), "USER NOT FOUND"));
    }*/

}
