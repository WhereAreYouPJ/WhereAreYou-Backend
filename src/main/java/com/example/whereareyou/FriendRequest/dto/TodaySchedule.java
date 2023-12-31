package com.example.whereareyou.FriendRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TodaySchedule {

    private List<ScheduleList> scheduleList;

}
