package com.sonofiroko.email.service;

public interface MessageService<T> {

    void send(T object) throws Exception;
}
