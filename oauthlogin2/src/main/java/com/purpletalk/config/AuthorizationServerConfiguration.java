package com.purpletalk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * Convenient strategy for configuring an OAUth2 Authorization Server. 
 * Beans of this type are applied to the Spring context automatically if you use @EnableAuthorizationServer.
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
 
    private static String REALM="MY_OAUTH_REALM";
    
    /**
     *  for OAuth2 tokens.
     */
    @Autowired
    private TokenStore tokenStore;
 
    /**
     * It checks whether a given client authentication request has been approved by the current user.
     */
    @Autowired
    private UserApprovalHandler userApprovalHandler;
 
    /**
     * Processes an Authentication request.
     */
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    
    /**
     * Configure the ClientDetailsService, e.g. declaring individual clients and their properties. 
     * Note that password grant is not enabled (even if some clients are allowed it) unless an AuthenticationManager is
	 * supplied to the #configure(AuthorizationServerEndpointsConfigurer). 
	 * At least one client, or a fully formed custom ClientDetailsService must be declared or the server will not start.
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
 
        clients.inMemory()
            .withClient("my-trusted-client")
            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
            .scopes("read", "write", "trust")
            .secret("secret")
            .accessTokenValiditySeconds(120).//Access token is only valid for 2 minutes.
            refreshTokenValiditySeconds(600);//Refresh token is only valid for 10 minutes.
    }
    
    /**
     * Configure the non-security features of the Authorization Server endpoints, like token store, token
	 * customizations, user approvals and grant types. 
	 * You shouldn't need to do anything by default, unless you need password grants, 
	 * in which case you need to provide an AuthenticationManager.
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager);
    }
    
    /**
     * Configure the security of the Authorization Server, which means in practical terms the /oauth/token endpoint.
     * he default settings cover the most common requirements, following recommendations from the OAuth2 spec, 
     * so you don't need to do anything here to get a basic server up and running.
     */
 
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.realm(REALM+"/client");
    }
 
}
