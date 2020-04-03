package common.manager.domain.service.oauth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import common.manager.domain.model.Privilege;
import common.manager.domain.model.Role;
import common.manager.domain.model.User;

public class CustomUserDetails extends User implements UserDetails {

    public CustomUserDetails(User user) {
        super(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authoritiesSet = new HashSet<>();
        for (Role role : super.getRoles()) {
            for (Privilege privilege : role.getPrivileges()) {
                authoritiesSet.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        }
        return authoritiesSet;
    }

    @Override
    public String getPassword() {
        return super.getCredential().getPassword();
    }

    @Override
    public String getUsername() {
        return super.getCredential().getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !super.getAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !super.getCredential().getCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
