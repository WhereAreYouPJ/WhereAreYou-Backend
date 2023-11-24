package com.example.whereareyou.schedule.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseSaveSchedule {
    private String scheduleId;
}
