package common.manager.domain.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.manager.domain.model.User;
import common.manager.domain.repository.UserRepository;

@Service
public abstract class CommonServiceImpl implements CommonService {

    @Autowired
    private UserRepository userRepository;

    public CommonServiceImpl() {
        super();
    }

    public User getUser(Long userId) {
        return userRepository.getOne(userId);
    }

}
