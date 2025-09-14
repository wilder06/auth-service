package pe.com.creditya.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.com.creditya.security.jwt.JwtProperties;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;

@Configuration
@RequiredArgsConstructor
public class JwtKeyConfig {

    private final JwtProperties props;

    @Bean
    public KeyPair jwtKeyPair() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(
                    new FileInputStream(props.getKeystoreLocation()),
                    props.getKeystorePassword().toCharArray()
            );

            Key key = keyStore.getKey(
                    props.getKeyAlias(),
                    props.getKeyPassword().toCharArray()
            );

            if (!(key instanceof PrivateKey privateKey)) {
                throw new IllegalStateException("No private key found for alias " + props.getKeyAlias());
            }

            Certificate cert = keyStore.getCertificate(props.getKeyAlias());
            PublicKey publicKey = cert.getPublicKey();

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load JWT key pair from keystore", e);
        }
    }
}

