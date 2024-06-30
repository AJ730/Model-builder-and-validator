package nl.tudelft.sp.modelchecker.config;

import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerSecurityConfig extends AADResourceServerWebSecurityConfigurerAdapter {

    /**
     * Configure http.
     * uses http.oauth2ResourceServer(),
     * which uses JwtDecoder in AADResourceServerConfiguration.java,
     * obtaining public key from Azure authorization server to validate the token.
     * https://www.baeldung.com/spring-security-oauth-resource-server.
     *
     * @param http http
     * @throws Exception Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        super.configure(http);

        http.authorizeRequests().antMatchers("/h2-console/**").permitAll()
                .antMatchers("/v2/api-docs",
                        "/Loader/ui",
                        "/swagger-resources/**",
                        "/Loader/security",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll()
                .anyRequest().authenticated().and().csrf().disable();
        http.cors();

        http.headers().frameOptions().sameOrigin();
    }
}
