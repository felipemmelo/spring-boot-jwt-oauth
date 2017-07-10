package security.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import parameters.Parameters;

/**
 * Provides a configured TokenStore instance.
 */
@Configuration
public class TokenStoreConfiguration {

	@Value(Parameters.KEY_TOKEN_SYMMETRIC_KEY)	
	private String symmetricKey;
	
	@Value(Parameters.KEY_JKS_FILE)
	private String jksFile;
	
	@Value(Parameters.KEY_JKS_ASYMMETRIC_PASSWORD)
	private String asymmetricPassword;
	
	@Value(Parameters.KEY_JKS_ASYMMETRIC_ALIAS)
	private String asymmetricAlias;
	
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverterAsymmetric());
    }

    /**
     * Creates a JwtAccessTokenConverter from a symmetric key.
     */
    @SuppressWarnings("unused")
	private JwtAccessTokenConverter jwtAccessTokenConverterSymmetric() {    	
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();   
        tokenConverter.setSigningKey(this.symmetricKey);
        return tokenConverter;
    }
    
    /**
     * Creates a JwtAccessTokenConverter from an RSA key.
     */
    private JwtAccessTokenConverter jwtAccessTokenConverterAsymmetric() {
    
      JwtAccessTokenConverter converter = new JwtAccessTokenConverter();        
      
      KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(this.jksFile), this.asymmetricPassword.toCharArray());
      
      converter.setKeyPair(keyStoreKeyFactory.getKeyPair(this.asymmetricAlias));
      
      return converter;
    }
}
