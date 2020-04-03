package common.manager.domain.service.oauth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import common.manager.domain.model.User;

public class CustomClientDetails extends User implements ClientDetails {

    public CustomClientDetails(User user) {
        super(user);
    }

    @Override
    public String getClientId() {
        return super.getCredential().getClientId();
    }

    @Override
    public String getClientSecret() {
        return super.getCredential().getPassword();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getScope() {
        String[] scopes = super.getCredential().getScope().split(",");
        return Arrays.stream(scopes).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        String[] grantTypes = super.getCredential().getGrantType().split(",");
        return Arrays.stream(grantTypes).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getResourceIds() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return Collections.emptySet();
    }

    @Override
    public boolean isScoped() {
        return true;
    }

    @Override
    public boolean isSecretRequired() {
        return false;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return 2000;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 2000 * 2;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.emptyMap();
    }

}
