package pe.com.creditya.security.jwt;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private long expiration;
    private String keystoreLocation;
    private String keystorePassword;
    private String keyAlias;
    private String keyPassword;
}
