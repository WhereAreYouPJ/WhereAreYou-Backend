package com.example.whereareyou.exception.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FriendRequestNotFoundException extends RuntimeException{
    public FriendRequestNotFoundException(String m) {
        super(m);
    }
}