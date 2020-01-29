package com.queue.application.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.ALREADY_REPORTED)
public class QueueException extends RuntimeException{
    public QueueException(String exception){
        super(exception);
    }
}
