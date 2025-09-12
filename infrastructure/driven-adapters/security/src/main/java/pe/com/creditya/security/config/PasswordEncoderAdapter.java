package pe.com.creditya.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.com.creditya.model.user.gateways.PasswordEncoderRepository;

@RequiredArgsConstructor
@Component
public class PasswordEncoderAdapter implements PasswordEncoderRepository {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

