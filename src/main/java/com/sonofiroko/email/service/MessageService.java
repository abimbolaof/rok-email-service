package com.sonofiroko.email.service;

import com.sonofiroko.email.model.Message;

public interface MessageService<T extends Message> {

    void send(T object) throws Exception;
}
