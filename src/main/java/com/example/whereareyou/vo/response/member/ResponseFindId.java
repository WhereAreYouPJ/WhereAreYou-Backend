package com.example.whereareyou.vo.response.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseFindId {

    private String userId;

}