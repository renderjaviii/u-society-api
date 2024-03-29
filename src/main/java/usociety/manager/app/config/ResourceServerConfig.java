package usociety.manager.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${config.access-token.signing-key}")
    private String signingKey;

    private static final RequestMatcher PUBLIC_ENDPOINTS_MATCHERS = new OrRequestMatcher(
            new AntPathRequestMatcher("v1/services/users/**", HttpMethod.GET.name()),
            new AntPathRequestMatcher("v1/services/users", HttpMethod.POST.name()),
            new AntPathRequestMatcher("v1/services/users/enable-account", HttpMethod.POST.name()),
            new AntPathRequestMatcher("v1/services/users/**/verify", HttpMethod.POST.name()),
            new AntPathRequestMatcher("v1/services/categories", HttpMethod.GET.name()),
            new AntPathRequestMatcher("v1/services/users/login", HttpMethod.POST.name()));

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors()
                .and().csrf().disable()
                .authorizeRequests()
                .requestMatchers(PUBLIC_ENDPOINTS_MATCHERS).permitAll()
                .and()
                .authorizeRequests()
                .requestMatchers(new AntPathRequestMatcher("**/services/**")).authenticated();
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(signingKey);
        return jwtAccessTokenConverter;
    }

}