package common.manager.app.config;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${config.access-token.signing-key}")
    private String jwtSigningKey;
    @Value("${config.access-token.validitity-seconds}")
    private int accessTokenValiditySeconds;
    @Value("${config.refresh-token.validitity-seconds}")
    private int refreshTokenValiditySeconds;

    private final ClientDetailsService customClientDetails;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService customUserDetailsService;

    @Autowired
    public AuthorizationServerConfig(ClientDetailsService customClientDetails,
                                     AuthenticationManager authenticationManager,
                                     UserDetailsService customUserDetailsService) {
        this.customClientDetails = customClientDetails;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) {
        configurer
                .authenticationManager(authenticationManager)
                .userDetailsService(customUserDetailsService)
                .tokenStore(tokenStore())
                .tokenServices(tokenServices())
                .accessTokenConverter(accessTokenConverter());
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
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(5);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(jwtSigningKey);
        return jwtAccessTokenConverter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());

        defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        defaultTokenServices.setSupportRefreshToken(TRUE);
        defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
        defaultTokenServices.setReuseRefreshToken(FALSE);

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(accessTokenConverter()));
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);

        defaultTokenServices.setClientDetailsService(customClientDetails);
        defaultTokenServices.setAuthenticationManager(authenticationManager);
        return defaultTokenServices;
    }

}