package com.example.whereareyou.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCode extends RuntimeException{
    public InvalidCode(String m) {
        super(m);
    }
}
