package com.example.whereareyou.refreshToken.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class UsedTokenException extends RuntimeException{
    public UsedTokenException(String m) {
        super(m);
    }
}
