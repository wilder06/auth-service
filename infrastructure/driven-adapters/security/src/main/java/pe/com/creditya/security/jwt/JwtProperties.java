package pe.com.creditya.security.jwt;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Setter
@Getter
    @Component
    @ConfigurationProperties(prefix = "jwt")
    public class JwtProperties {
        private String secret;
        private long expiration;
    }
