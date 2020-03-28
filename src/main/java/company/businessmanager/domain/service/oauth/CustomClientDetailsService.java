package company.businessmanager.domain.service.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import company.businessmanager.domain.repository.UserRepository;

@Primary
@Service
public class CustomClientDetailsService implements ClientDetailsService {

    private static final String ERROR_MESSAGE_FORMAT = "The client %s is not valid.";

    private final UserRepository userRepository;

    @Autowired
    public CustomClientDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return userRepository.findByDocumentNumber(clientId)
                .map(CustomClientDetails::new)
                .orElseThrow(() -> new ClientRegistrationException(String.format(ERROR_MESSAGE_FORMAT, clientId)));
    }

}
