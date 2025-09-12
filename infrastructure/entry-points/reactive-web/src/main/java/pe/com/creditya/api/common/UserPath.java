package pe.com.creditya.api.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class UserPath {
    private String users;
    private String userByDocumentNumber;
    private String login;
    private String usersByEmails;
}