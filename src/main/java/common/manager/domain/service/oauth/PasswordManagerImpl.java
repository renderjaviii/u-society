package common.manager.domain.service.oauth;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import common.manager.domain.model.User;
import common.manager.domain.repository.UserRepository;

@Service
public class PasswordManagerImpl implements PasswordManager {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final Clock clock;

    @Autowired
    public PasswordManagerImpl(PasswordEncoder passwordEncoder,
                               UserRepository userRepository,
                               Clock clock) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public User checkPassword(User user, String rawPassword) {
        if (user.isAccountLocked()) {
            throw new LockedException("Account locked");
        }
        if (user.isEmailVerified()) {
            throw new DisabledException("Email not verified.");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        user.setLastAccessAt(LocalDateTime.now(clock));
        userRepository.saveAndFlush(user);

        return user;
    }

}
