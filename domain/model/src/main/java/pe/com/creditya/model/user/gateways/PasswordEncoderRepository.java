package pe.com.creditya.model.user.gateways;

public interface PasswordEncoderRepository {
    String encode(String password);
    boolean matches(String password, String encodedPassword);

}
