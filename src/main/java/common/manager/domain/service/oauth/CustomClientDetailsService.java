package common.manager.domain.service.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import common.manager.domain.repository.CredentialRepository;
import common.manager.domain.repository.UserRepository;

@Primary
@Service
public class CustomClientDetailsService implements ClientDetailsService, UserDetailsService {

    private static final String ERROR_MESSAGE_FORMAT = "The client %s is not valid.";
    private static final String ERROR_MESSAGE = "The username %s is not valid.";

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomClientDetailsService(CredentialRepository credentialRepository,
                                      UserRepository userRepository) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return credentialRepository.findByClientId(clientId)
                .map(CustomClientDetails::new)
                .orElseThrow(() -> new ClientRegistrationException(String.format(ERROR_MESSAGE_FORMAT, clientId)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(ERROR_MESSAGE, username)));
    }

}
