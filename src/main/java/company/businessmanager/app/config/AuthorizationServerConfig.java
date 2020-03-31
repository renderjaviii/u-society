package company.businessmanager.app.config;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String SIGNING_KEY = "123";

    private final ClientDetailsService customClientDetails;
    private final UserDetailsService customUserDetails;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuthorizationServerConfig(ClientDetailsService customClientDetails,
                                     UserDetailsService customUserDetails,
                                     AuthenticationManager authenticationManager,
                                     PasswordEncoder passwordEncoder) {
        this.customClientDetails = customClientDetails;
        this.customUserDetails = customUserDetails;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
        configurer
                .authenticationManager(authenticationManager)
                .userDetailsService(customUserDetails)
                .tokenServices(tokenServices())
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
        ;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(customClientDetails);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        //See signer key
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setVerifierKey("123");
        jwtAccessTokenConverter.setSigningKey("123");

        return jwtAccessTokenConverter;

    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());

        defaultTokenServices.setSupportRefreshToken(TRUE);
        defaultTokenServices.setRefreshTokenValiditySeconds(1234 * 2);
        defaultTokenServices.setReuseRefreshToken(FALSE);
        defaultTokenServices.setAccessTokenValiditySeconds(1234);

        defaultTokenServices.setClientDetailsService(customClientDetails);
        defaultTokenServices.setAuthenticationManager(authenticationManager);
        return defaultTokenServices;
    }

}