package com.sonofiroko.email.service;

import com.sonofiroko.email.model.EmailMessage;

public interface MessageService<T extends EmailMessage> {

    void send(T object) throws Exception;
}
