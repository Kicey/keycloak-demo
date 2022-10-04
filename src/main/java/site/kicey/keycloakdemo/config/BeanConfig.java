package site.kicey.keycloakdemo.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kicey
 */
@Configuration
public class BeanConfig {
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver(){
        return new KeycloakSpringBootConfigResolver();
    }
}
