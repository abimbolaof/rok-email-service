package com.sonofiroko.email.service;

import com.sonofiroko.email.model.Message;
import org.springframework.stereotype.Service;

@Service
public class SMSMessageService  implements MessageService<Message> {

    @Override
    public void send(Message object) throws Exception {

    }
}
