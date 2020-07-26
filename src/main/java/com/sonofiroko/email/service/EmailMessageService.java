package com.sonofiroko.email.service;

import com.sonofiroko.email.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailMessageService implements MessageService<Message>{

    private final static Logger log = LoggerFactory.getLogger(EmailMessageService.class);
    private Properties props;

    private static String HOST, USERNAME, PASSWORD;

    @Autowired
    EmailMessageService(@Value("${aws.smtp.username}") String username,
                        @Value("${aws.smtp.password}") String password,
                        @Value("${aws.smtp.host}") String host,
                        @Value("${aws.smtp.port}") int port,
                        Environment env) {

        props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        HOST = host;
        USERNAME = username;
        PASSWORD = password;

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
            transport.connect(HOST, USERNAME, PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            //transport.close();
        } catch (Exception ex) {
            //log exception here
            log.error(ex.getMessage());
            throw ex;
        }
    }
}
