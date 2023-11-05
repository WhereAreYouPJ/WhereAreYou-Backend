package com.example.whereareyou.schedule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * packageName    : project.whereareyou.exception.customexception
 * fileName       : ScheduleController
 * author         : pjh57
 * date           : 2023-09-16
 * description    : FriendListNotFoundException
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-09-16        pjh57       최초 생성
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FriendListNotFoundException extends RuntimeException{
    public FriendListNotFoundException(String m) {
        super(m);
    }
}