package com.sonofiroko.email.controller;


import com.sonofiroko.email.model.ApiException;
import com.sonofiroko.email.model.Message;
import com.sonofiroko.email.model.dto.PostMessage;
import com.sonofiroko.email.service.AsyncMessageService;
import com.sonofiroko.email.service.MessageTemplateProvider;
import com.sonofiroko.email.types.MessageTemplateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private AsyncMessageService<Message> service;

    @PostMapping(value = "/message")
    public void send(@RequestBody PostMessage msg) throws ApiException {
        Message message = new Message();
        message.setFrom(msg.getFrom());
        message.setTo(msg.getTo());
        message.setSubject(msg.getSubject());
        message.setTemplateType(MessageTemplateType.valueOf(msg.getType()));
        MessageTemplateProvider.newInstance().setValues(msg.getValues()).apply(message);

        service.send(message);
    }
}
