package security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

/**
 * Implements the Authorization server from in-memory (hard-coded) configuration.
 *
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("userDetailsService")
	private UserDetailsService userDetailsService;

	@Value("${oauth.expire.seconds}")
	private int expirationSeconds;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JdbcClientDetailsService clientDetailsService;
	
	@Bean
	@Autowired
	public PasswordEncoder passwordEncoder(@Value("${jwt.secret}") String secret) {
		return new StandardPasswordEncoder(secret);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
		configurer.authenticationManager(authenticationManager);
		configurer.userDetailsService(userDetailsService);
	}
	
	@Bean
	@Autowired
	public JdbcClientDetailsService clientDetailsService(DataSource dataSource) {
		return new JdbcClientDetailsService(dataSource);
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {				

		clients.withClientDetails(this.clientDetailsService);
	}
	
//	@Override
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {				
//				
//		clients.inMemory()
//		.withClient("userId1")
//			.secret("passId1")
//			.accessTokenValiditySeconds(expirationSeconds)
//			.authorities("ADMIN", "USER")
//			.scopes("read")
//			.authorizedGrantTypes("password", "refresh_token", "client_credentials")
//			.resourceIds(Parameters.RESOURCE_ID)
//		.and()
//		.withClient("userId2")
//			.secret("passId2")		
//			.accessTokenValiditySeconds(expirationSeconds)
//			.authorities("USER")
//			.scopes("read")
//			.authorizedGrantTypes("password", "refresh_token", "client_credentials")
//			.resourceIds(Parameters.RESOURCE_ID);	
//	}
}