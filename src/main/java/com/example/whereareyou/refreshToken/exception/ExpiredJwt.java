package com.example.whereareyou.refreshToken.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ExpiredJwt extends RuntimeException{
    public ExpiredJwt(String m){
        super(m);
    }
}

