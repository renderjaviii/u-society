package company.businessmanager.domain.service.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import company.businessmanager.domain.model.Credential;
import company.businessmanager.domain.repository.CredentialRepository;
import company.businessmanager.domain.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String ERROR_MESSAGE_FORMAT = "The username %s is not valid.";
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    CredentialRepository credentialRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Credential userCredential = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(ERROR_MESSAGE_FORMAT, username)));

        return userRepository.findByDocumentNumber(userCredential.getClientId())
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(ERROR_MESSAGE_FORMAT, username)));
    }

}
