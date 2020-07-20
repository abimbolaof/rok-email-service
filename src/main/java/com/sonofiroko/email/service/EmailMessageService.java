package com.sonofiroko.email.service;

import com.sonofiroko.email.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailMessageService implements MessageService<Message>{

    private final static Logger log = LoggerFactory.getLogger(EmailMessageService.class);
    @Autowired
    private Environment env;
    private Properties props;

    private final String SMTP_USERNAME;
    private final String SMTP_PASSWORD;
    private final String SMTP_HOST;
    private final int SMTP_PORT;

    @Autowired
    EmailMessageService(@Value("aws.smtp.username") String username,
                        @Value("aws.smtp.password") String password,
                        @Value("aws.smtp.host") String host,
                        @Value("aws.smtp.port") int port) {
        SMTP_USERNAME = username;
        SMTP_PASSWORD = password;
        SMTP_HOST = host;
        SMTP_PORT = port;

        props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
    }

    @Override
    public void send(Message message) throws Exception{
        try {
            Session session = Session.getDefaultInstance(props);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(message.getFrom()));
            msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(message.getTo()));
            msg.setSubject(message.getSubject());
            msg.setContent(message.getBody(), "text/html");

            Transport transport = session.getTransport();
            transport.connect(SMTP_HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            //transport.close();
        } catch (Exception ex) {
            //log exception here
            log.error(ex.getMessage());
            throw ex;
        }
    }
}
