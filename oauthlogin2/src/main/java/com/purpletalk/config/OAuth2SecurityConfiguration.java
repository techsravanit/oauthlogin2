package com.purpletalk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Allows customization to the WebSecurity. 
 * In most instances users will use @EnableWebSecurity and a create Configuration 
 * that extends WebSecurityConfigurerAdapter. 
 */
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * A service that provides the details about an OAuth2 client.
	 */
	@Autowired
	private ClientDetailsService clientDetailsService;

	/**
	 * Used by the default implementation of #authenticationManager() to attempt to obtain an AuthenticationManager. 
	 * If overridden, the AuthenticationManagerBuilder should be used to specify the AuthenticationManager.
	 * The #authenticationManagerBean() method can be used to expose the resulting AuthenticationManager as a Bean.
	 * The #userDetailsServiceBean() can be used to expose the last populated UserDetailsService that is created
	 * with the AuthenticationManagerBuilder as a Bean. 
	 * The UserDetailsService will also automatically be populated on HttpSecurity#getSharedObject(Class)
	 * for use with other SecurityContextConfigurer} (i.e. RememberMeConfigurer )
	 * 
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
		.withUser("admin").password("admin").roles("ADMIN").and()
		.withUser("user").password("user").roles("USER");
	}

	/**
	 * Override this method to configure the HttpSecurity. 
	 * Typically subclasses should not invoke this method by calling super as it may override their configuration.
	 * The default configuration is:
	 *	 http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic(); 
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.anonymous().disable()
		.authorizeRequests()
		.antMatchers("/oauth/token").permitAll();
	}

	/**
	 * Override this method to expose the AuthenticationManager from
	 * #configure(AuthenticationManagerBuilder) to be exposed as a Bean.
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * InMemoryTokenStore -- Implementation of token services that stores tokens in memory.
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	/**
	 * TokenStoreUserApprovalHandler -- A user approval handler that remembers approval decisions by consulting existing tokens.
	 * setClientDetailsService -- Service to load client details (optional) for auto approval checks.
	 * @param tokenStore
	 * @return
	 */
	@Bean
	@Autowired
	public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore){
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore);
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}

	/* To avoid the below exception
			There is no PasswordEncoder mapped for the id null */
	@SuppressWarnings("deprecation")
	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}

	/**
	 * ApprovalStore -- for saving, retrieving and revoking user approvals (per client, per scope).
	 * TokenApprovalStore -- An ApprovalStore that works with an existing TokenStore, extracting implicit Approvals from the content of tokens already in the store. 
	 * Useful interface so that users can list and revoke approvals even if they are not really represented in such a way internally.
	 *
	 * @param tokenStore
	 * @return
	 * @throws Exception
	 */
	/*  @Bean
    @Autowired
    public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);
        return store;
    }*/

	/**
	 * Allows modifying and accessing the UserDetailsService from #userDetailsServiceBean() 
	 * without interacting with the ApplicationContext. 
	 * Developers should override this method when changing the instance of #userDetailsServiceBean().
	 */
	/*  @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password("user").roles("USER").build());
        return manager;
    }*/

}