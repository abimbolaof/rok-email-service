package com.sonofiroko.email.service;

import com.sonofiroko.email.model.EmailMessage;
import org.springframework.stereotype.Service;

@Service
public class SMSMessageService  implements MessageService<EmailMessage> {

    @Override
    public void send(EmailMessage object) throws Exception {

    }
}
