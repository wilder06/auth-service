package pe.com.creditya.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import pe.com.creditya.security.common.constants.ApiPaths;
import pe.com.creditya.security.jwt.JwtAuthenticationManager;
import pe.com.creditya.security.repository.SecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(ApiPaths.API_DOCS_ALL,
                                ApiPaths.SWAGGER_UI,
                                ApiPaths.SWAGGER_UI_ALL,
                                ApiPaths.WEBJARS_ALL,
                                ApiPaths.SWAGGER_RESOURCES_ALL).permitAll()
                        .pathMatchers(HttpMethod.GET, ApiPaths.FIND_USER_BY_DOCUMENT_NUMBER).permitAll()
                        .pathMatchers(HttpMethod.POST, ApiPaths.LOGIN).permitAll()
                        .pathMatchers(HttpMethod.POST, ApiPaths.REGISTER).authenticated()
                        .pathMatchers(HttpMethod.POST, ApiPaths.APPLICATIONS).authenticated()
                        .anyExchange().authenticated()
                )
               .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authenticationManager(jwtAuthenticationManager)
                .securityContextRepository(securityContextRepository)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
