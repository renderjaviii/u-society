package common.manager.domain.service.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import common.manager.domain.repository.CredentialRepository;

@Primary
@Service
public class CustomClientDetailsService implements ClientDetailsService {

    private static final String ERROR_MESSAGE_FORMAT = "The client id %s is not valid.";

    private final CredentialRepository credentialRepository;

    @Autowired
    public CustomClientDetailsService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return credentialRepository.findByClientId(clientId)
                .map(CustomClientDetails::new)
                .orElseThrow(() -> new ClientRegistrationException(String.format(ERROR_MESSAGE_FORMAT, clientId)));
    }

}
