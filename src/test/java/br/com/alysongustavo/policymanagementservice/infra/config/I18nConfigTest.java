package br.com.alysongustavo.policymanagementservice.infra.config;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = I18nConfig.class)
public class I18nConfigTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    @DisplayName("Should load messages from messages properties file correctly")
    void shouldLoadMessagesFromPropertiesFile() {
        String message = messageSource.getMessage("test.welcome", null, Locale.getDefault());

        assertThat(message).isEqualTo("Welcome");
    }

    @Test
    @DisplayName("Should respect UTF-8 encoding for special characters")
    void shouldRespectUtf8Encoding() {
        String message = messageSource.getMessage("test.utf8", null, Locale.US);

        assertThat(message).isEqualTo("Accented characters can be tricky: Ção");
    }

    @Test
    @DisplayName("Should return default message when key does not exist")
    void shouldReturnDefaultMessageWhenKeyDoesNotExist() {
        String result = messageSource.getMessage(
                "non.existing.key",
                null,
                "Default Message",
                Locale.US
        );

        assertThat(result).isEqualTo("Default Message");
    }
}
