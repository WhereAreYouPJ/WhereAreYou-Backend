package com.example.whereareyou.schedule.controller;

import com.example.whereareyou.schedule.request.*;
import com.example.whereareyou.schedule.response.*;
import com.example.whereareyou.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 일정 추가
     *
     * @param requestSaveSchedule the request save schedule
     * @return the response entity
     */
    @PostMapping()
    public ResponseEntity<ResponseSaveSchedule> saveSchedule(@RequestBody RequestSaveSchedule requestSaveSchedule){
        ResponseSaveSchedule savedSchedule = scheduleService.save(requestSaveSchedule);

        return ResponseEntity.status(HttpStatus.OK).body(savedSchedule);
    }

    /**
     * 일정 수정
     *
     * @param requestModifySchedule the request modify schedule
     * @return the response entity
     */
    @PutMapping()
    public ResponseEntity<Void> modifySchedule(@RequestBody RequestModifySchedule requestModifySchedule){
        scheduleService.modifySchedule(requestModifySchedule);

        return ResponseEntity.noContent().build();
    }

    /**
     * 월별 일정 정보
     *
     * @param memberId the member id
     * @param year     the year
     * @param month    the month
     * @return the response entity
     */
    @GetMapping("/month")
    public ResponseEntity<ResponseMonthlySchedule> getMonthSchedule(@RequestParam
                                                                    String memberId,
                                                                    Integer year,
                                                                    Integer month){
        ResponseMonthlySchedule responseMonthlySchedule
                = scheduleService.getMonthSchedule(memberId, year, month);

        return ResponseEntity.status(HttpStatus.OK).body(responseMonthlySchedule);
    }

    /**
     * 일정 삭제
     *
     * @param requestDeleteSchedule the request delete schedule
     * @return the response entity
     */
    @DeleteMapping()
    public ResponseEntity<Void> deleteSchedule(@RequestBody RequestDeleteSchedule requestDeleteSchedule){
        scheduleService.deleteSchedule(requestDeleteSchedule);

        return ResponseEntity.noContent().build();
    }

    /**
     * 일별 일정 간략 정보
     *
     * @param memberId the member id
     * @param year     the year
     * @param month    the month
     * @param date     the date
     * @return the response entity
     */
    @GetMapping("/date")
    public ResponseEntity<ResponseBriefDateSchedule> getBriefDateSchedule(@RequestParam
                                                                          String memberId,
                                                                          Integer year,
                                                                          Integer month,
                                                                          Integer date){
        ResponseBriefDateSchedule briefDateSchedule = scheduleService.getBriefDateSchedule(memberId, year, month, date);

        return ResponseEntity.status(HttpStatus.OK).body(briefDateSchedule);
    }

    /**
     * 일별 일정 상세 정보
     *
     * @param memberId   the member id
     * @param scheduleId the schedule id
     * @return the response entity
     */
    @GetMapping("/details")
    public ResponseEntity<ResponseDetailSchedule> getDetailSchedule(@RequestParam
                                                                    String memberId,
                                                                    String scheduleId){
        ResponseDetailSchedule responseDetailSchedule = scheduleService.getDetailSchedule(memberId, scheduleId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDetailSchedule);
    }

    /**
     * 도착 여부
     *
     * @param requestScheduleArrived the request schedule arrived
     * @return the response entity
     */
    @PutMapping("/arrived")
    public ResponseEntity<Boolean> scheduleArrived(@RequestBody RequestScheduleArrived requestScheduleArrived){
        scheduleService.scheduleArrived(requestScheduleArrived);

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @GetMapping("/today")
    public ResponseEntity<ResponseTodaySchedule> todaySchedule(@RequestParam String memberId) {
        ResponseTodaySchedule todaySchedule = scheduleService.getTodaySchedule(memberId);

        return ResponseEntity.ok().body(todaySchedule);
    }

    /**
     * 캘랜더 삭제
     *
     * @param requestResetSchedule
     * @return
     */
    @DeleteMapping("/reset")
    public ResponseEntity<Boolean> resetSchedule(@RequestBody RequestResetSchedule requestResetSchedule){
        scheduleService.resetSchedule(requestResetSchedule);

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}