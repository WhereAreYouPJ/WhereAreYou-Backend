package com.example.whereareyou.member.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMemberByUserId {

    private String userName;
    private String userId;
    private String profileImage;
}
