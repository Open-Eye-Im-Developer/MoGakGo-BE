package io.oeid.mogakgo.core.configuration;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String HOST;

    @Value("${spring.mail.port}")
    private int PORT;

    @Value("${spring.mail.username}")
    private String USERNAME;

    @Value("${spring.mail.password}")
    private String PASSWORD;

    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String TRANSPORT_PROTOCOL;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String SMTP_AUTH;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String SMTP_STARTTLS_ENABLE;

    @Value("${spring.mail.properties.mail.debug}")
    private String DEBUG;


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", TRANSPORT_PROTOCOL);
        props.put("mail.smtp.auth", SMTP_AUTH);
        props.put("mail.smtp.starttls.enable", SMTP_STARTTLS_ENABLE);
        props.put("mail.debug", DEBUG);

        return mailSender;
    }
}
