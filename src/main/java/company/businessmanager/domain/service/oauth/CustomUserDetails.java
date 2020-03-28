package company.businessmanager.domain.service.oauth;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import company.businessmanager.domain.model.Privilege;
import company.businessmanager.domain.model.Role;
import company.businessmanager.domain.model.User;

public class CustomUserDetails extends User implements UserDetails {

    public CustomUserDetails(User user) {
        super(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authoritiesList = new LinkedList<>();
        for (Role role : super.getRoles()) {
            for (Privilege privilege : role.getPrivileges()) {
                authoritiesList.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        }
        return authoritiesList;
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
