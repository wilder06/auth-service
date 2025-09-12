package pe.com.creditya.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pe.com.creditya.model.common.validations.UserValidator;
import pe.com.creditya.model.token.gateways.TokenRepository;
import pe.com.creditya.model.user.gateways.PasswordEncoderRepository;
import pe.com.creditya.model.user.gateways.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public UserRepository taskRepository() {
            return Mockito.mock(UserRepository.class);
        }
        @Bean
        public UserValidator userValidator() {
            return Mockito.mock(UserValidator.class);
        }
        @Bean
        public TokenRepository tokenRepository() {
            return Mockito.mock(TokenRepository.class);
        }
        @Bean

        public PasswordEncoderRepository passwordEncoderRepository() {
            return Mockito.mock(PasswordEncoderRepository.class);
        }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}