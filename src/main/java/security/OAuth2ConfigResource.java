package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import parameters.Parameters;

@Configuration
@EnableResourceServer
public class OAuth2ConfigResource extends ResourceServerConfigurerAdapter {

	@Autowired
	private TokenStore tokenStore;
	
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers()
                .antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers("/api/secure/**").access("#oauth2.clientHasRole('ADMIN')")
                .antMatchers("/api/public/**").access("#oauth2.clientHasRole('USER')");
    }
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(this.tokenStore);
        resources.resourceId(Parameters.RESOURCE_ID);
    }


}
