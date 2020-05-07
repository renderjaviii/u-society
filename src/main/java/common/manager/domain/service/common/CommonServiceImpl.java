package common.manager.domain.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.manager.domain.exception.GenericException;
import common.manager.domain.model.User;
import common.manager.domain.repository.UserRepository;

@Service
public abstract class CommonServiceImpl implements CommonService {

    private static final String USER_NOT_FOUND_FORMAT = "Username: %s not found.";

    @Autowired
    private UserRepository userRepository;

    public CommonServiceImpl() {
        super();
    }

    public User getUser(Long userId) {
        return userRepository.getOne(userId);
    }

    public User getUser(String username) throws GenericException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GenericException(String.format(USER_NOT_FOUND_FORMAT, username), "USER NOT FOUND"));
    }

}
