package com.example.whereareyou.service;

import com.example.whereareyou.domain.Member;
import com.example.whereareyou.domain.MemberSchedule;
import com.example.whereareyou.domain.Schedule;
import com.example.whereareyou.dto.BriefDateScheduleDTO;
import com.example.whereareyou.dto.MonthlyScheduleResponseDTO;
import com.example.whereareyou.exception.customexception.*;
import com.example.whereareyou.repository.MemberRepository;
import com.example.whereareyou.repository.MemberScheduleRepository;
import com.example.whereareyou.repository.ScheduleRepository;
import com.example.whereareyou.vo.request.schedule.RequestDeleteSchedule;
import com.example.whereareyou.vo.request.schedule.RequestModifySchedule;
import com.example.whereareyou.vo.request.schedule.RequestSaveSchedule;
import com.example.whereareyou.vo.response.schedule.ResponseBriefDateSchedule;
import com.example.whereareyou.vo.response.schedule.ResponseDetailSchedule;
import com.example.whereareyou.vo.response.schedule.ResponseMonthlySchedule;
import com.example.whereareyou.vo.response.schedule.ResponseSaveSchedule;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : project.whereareyou.service
 * fileName       : ScheduleService
 * author         : pjh57
 * date           : 2023-09-16
 * description    : 일정 관련 비즈니스 로직
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-09-16        pjh57       최초 생성
 */
@Service
@Transactional
public class ScheduleService {
    private final MemberScheduleRepository memberScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    /**
     * Instantiates a new Schedule service.
     *
     * @param memberScheduleRepository the member schedule repository
     * @param scheduleRepository       the schedule repository
     * @param memberRepository         the member repository
     */
    @Autowired
    public ScheduleService(MemberScheduleRepository memberScheduleRepository, ScheduleRepository scheduleRepository, MemberRepository memberRepository) {
        this.memberScheduleRepository = memberScheduleRepository;
        this.scheduleRepository = scheduleRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Save response save schedule.
     *
     * @param requestSaveSchedule the request save schedule
     * @return the response save schedule
     */
    public ResponseSaveSchedule save(RequestSaveSchedule requestSaveSchedule) {
        /*
         예외처리
         404 UserNotFoundException: MemberId Not Found
         400 FriendListNotFoundException: FriendListNot Found
         400 MemberIdCannotBeInFriendListException: FriendList have creatorId
         401: Unauthorized (추후에 추가할 예정)
         500: Server
        */
        Member creator = memberRepository.findById(requestSaveSchedule.getMemberId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 memberId입니다."));

        List<String> friendList = requestSaveSchedule.getMemberIdList();
        if (friendList == null || friendList.isEmpty())
            throw new FriendListNotFoundException("일정 추가 시 친구 설정은 필수 입니다.");

        if (friendList.contains(creator.getId()))
            throw new MemberIdCannotBeInFriendListException("일정 친구 추가 시 본인의 ID는 들어갈 수 없습니다.");

        List<Member> friends = memberRepository.findAllById(friendList);
        if (friends.size() != friendList.size()) {
            throw new UserNotFoundException("존재하지 않는 memberId입니다.");
        }

        // Schedule Entity 생성
        Schedule schedule = Schedule.builder()
                .start(requestSaveSchedule.getStart())
                .end(requestSaveSchedule.getEnd())
                .title(requestSaveSchedule.getTitle())
                .place(requestSaveSchedule.getPlace())
                .memo(requestSaveSchedule.getMemo())
                .closed(false)
                .creator(creator)
                .build();
        // Schedule 저장
        scheduleRepository.save(schedule);


        // friends 리스트를 사용하여 MemberSchedule 엔터티 생성 및 저장
        List<MemberSchedule> memberSchedules = friends.stream()
                .map(friend -> MemberSchedule.builder()
                        .schedule(schedule)
                        .member(friend)
                        .accept(false)
                        .build())
                .collect(Collectors.toList());
        // MemberSchedule 저장
        memberScheduleRepository.saveAll(memberSchedules);

        // ResponseSaveSchedule 생성
        ResponseSaveSchedule responseSaveSchedule = new ResponseSaveSchedule();
        responseSaveSchedule.setScheduleId(schedule.getId());

        return responseSaveSchedule;
    }

    /**
     * Modify schedule.
     *
     * @param requestModifySchedule the request modify schedule
     */
    public void modifySchedule(RequestModifySchedule requestModifySchedule) {
        /*
         예외처리
         404 ScheduleNotFoundException: ScheduleId Not Found
         404 UserNotFoundException: MemberId Not Found
         400 FriendListNotFoundException: FriendListNot Found
         400 MemberIdCannotBeInFriendListException: FriendList have creatorId
         401: Unauthorized (추후에 추가할 예정)
         500 updateQueryException: update Fail
         500: Server
        */
        Member modifier = memberRepository.findById(requestModifySchedule.getMemberId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 memberId입니다."));
        Schedule savedSchedule = scheduleRepository.findById(requestModifySchedule.getScheduleId())
                .orElseThrow(() -> new ScheduleNotFoundException("존재하지 않는 scheduleId입니다."));

        List<String> friendList = requestModifySchedule.getMemberIdList();
        if (friendList == null || friendList.isEmpty())
            throw new FriendListNotFoundException("일정 추가 시 친구 설정은 필수 입니다.");

        if (friendList.contains(modifier.getId()))
            throw new MemberIdCannotBeInFriendListException("일정 친구 추가 시 본인의 ID는 들어갈 수 없습니다.");

        List<Member> friends = memberRepository.findAllById(friendList);
        if (friends.size() != friendList.size())
            throw new UserNotFoundException("존재하지 않는 memberId입니다.");

        // Schedule 변경
        int updatedCount = scheduleRepository.updateSchedule(
                requestModifySchedule.getStart(),
                requestModifySchedule.getEnd(),
                requestModifySchedule.getTitle(),
                requestModifySchedule.getPlace(),
                requestModifySchedule.getMemo(),
                savedSchedule.getClosed(),
                modifier,
                savedSchedule.getId()
        );

        if (updatedCount == 0)
            throw new UpdateQueryException("업데이트 실패");

        // 기존의 MemberSchedule을 모두 삭제
        memberScheduleRepository.deleteAllBySchedule(savedSchedule);

        // friendList에 대한 MemberSchedule 생성 및 저장
        for (String memberId : friendList) {
            Member scheduleMember = memberRepository.findById(memberId).orElseThrow(
                    () -> new UserNotFoundException("존재하지 않는 memberId입니다."));
            MemberSchedule newMemberSchedule = MemberSchedule.builder()
                    .schedule(savedSchedule)
                    .member(scheduleMember)
                    .accept(false)
                    .build();
            memberScheduleRepository.save(newMemberSchedule);
        }
    }

    /**
     * Get month schedule response monthly schedule.
     *
     * @param memberId the member id
     * @param year     the year
     * @param month    the month
     * @return the response monthly schedule
     */
    public ResponseMonthlySchedule getMonthSchedule(String memberId, Integer year, Integer month){
        /*
         예외처리
         404 UserNotFoundException: MemberId Not Found
         400 InvalidYearOrMonthOrDateException: Invalid Year or Month or Date
         401: Unauthorized (추후에 추가할 예정)
         500: Server
        */
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 memberId입니다."));

        if(month < 1 || month > 12)
            throw new InvalidYearOrMonthOrDateException("월이 올바르지 않습니다.");

        // 해당 Member가 생성한 Schedule 반환
        List<Schedule> scheduleList = member.getScheduleList();

        // Response 객체 생성
        ResponseMonthlySchedule responseMonthlySchedule = new ResponseMonthlySchedule();
        responseMonthlySchedule.setYear(year);
        responseMonthlySchedule.setMonth(month);
        responseMonthlySchedule.setSchedules(new ArrayList<>());

        // Month의 최대 일수 구하기
        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        // 일별로 스케줄이 있는지 확인
        for (int day = 1; day <= daysInMonth; day++) {
            MonthlyScheduleResponseDTO monthlyScheduleResponseDTO = new MonthlyScheduleResponseDTO();
            monthlyScheduleResponseDTO.setDate(day);

            int finalDay = day;
            List<Schedule> schedulesForTheDay = scheduleList.stream().filter(schedule -> {
                LocalDate currentDate = LocalDate.of(year, month, finalDay);
                LocalDate scheduleStartDate = schedule.getStart().toLocalDate();
                LocalDate scheduleEndDate = schedule.getEnd().toLocalDate();
                return (currentDate.isEqual(scheduleStartDate) || currentDate.isAfter(scheduleStartDate)) &&
                        (currentDate.isEqual(scheduleEndDate) || currentDate.isBefore(scheduleEndDate));
            }).collect(Collectors.toList());

            monthlyScheduleResponseDTO.setHasSchedule(!schedulesForTheDay.isEmpty());
            monthlyScheduleResponseDTO.setAmountSchedule(schedulesForTheDay.size());
            responseMonthlySchedule.getSchedules().add(monthlyScheduleResponseDTO);
        }

        return responseMonthlySchedule;
    }

    /**
     * Delete schedule.
     *
     * @param requestDeleteSchedule the request delete schedule
     */
    public void deleteSchedule(RequestDeleteSchedule requestDeleteSchedule){
        /*
         예외처리
         404: ScheduleId Not Found
         401: Unauthorized (추후에 추가할 예정)
         500: Server
        */
        Schedule schedule = scheduleRepository.findById(requestDeleteSchedule.getScheduleId())
                .orElseThrow(() -> new ScheduleNotFoundException("존재하지 않는 scheduleId입니다."));

        memberScheduleRepository.deleteAllBySchedule(schedule);
        scheduleRepository.deleteById(requestDeleteSchedule.getScheduleId());
    }

    /**
     * Get brief date schedule response brief date schedule.
     *
     * @param memberId the member id
     * @param year     the year
     * @param month    the month
     * @param date     the date
     * @return the response brief date schedule
     */
    public ResponseBriefDateSchedule getBriefDateSchedule(String memberId, Integer year, Integer month, Integer date){
        /*
         예외처리
         404: MemberId Not Found
         400: Invalid Year or Month or Date
         401: Unauthorized (추후에 추가할 예정)
         500: Server
        */
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 memberId입니다."));

        // Response할 객체 생성
        ResponseBriefDateSchedule responseBriefDateSchedule = new ResponseBriefDateSchedule();
        responseBriefDateSchedule.setBriefDateScheduleDTOList(new ArrayList<>());

        // Member를 통해 ScheduleList 반환
        List<Schedule> scheduleList = findMember.getScheduleList();
        for(Schedule schedule : scheduleList){
            // 스케줄의 시작날짜가 입력한 연, 월, 일과 일치하는지 확인
            if(schedule.getStart().getYear() == year
                    && schedule.getStart().getMonthValue() == month
                    && schedule.getStart().getDayOfMonth() == date){

                // ModelMapper를 사용하여 BriefDateScheduleDTO로 변환
                ModelMapper mapper = new ModelMapper();
                mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

                BriefDateScheduleDTO briefDateScheduleDTO = mapper.map(schedule, BriefDateScheduleDTO.class);
                briefDateScheduleDTO.setScheduleId(schedule.getId());
                responseBriefDateSchedule.getBriefDateScheduleDTOList().add(briefDateScheduleDTO);
            }
        }

        return responseBriefDateSchedule;
    }

    /**
     * Get detail schedule response detail schedule.
     *
     * @param memberId   the member id
     * @param scheduleId the schedule id
     * @return the response detail schedule
     */
    public ResponseDetailSchedule getDetailSchedule(String memberId, String scheduleId){
        /*
         예외처리
         404 UserNotFoundException: MemberId Not Found
         404 ScheduleNotFoundException: scheduleId Not Found
         400 NotCreatedScheduleByMemberException: This is not a user-created schedule
         401: Unauthorized (추후에 추가할 예정)
         500: Server
        */
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 memberId입니다."));
        Schedule findSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("존재하지 않는 scheduleId입니다."));

        List<Schedule> scheduleList = findMember.getScheduleList();
        if(!scheduleList.contains(findSchedule))
            throw new NotCreatedScheduleByMemberException("회원이 만든 일정이 아닙니다.");

        // 친구의 ID 목록 추출
        List<String> friendsIdList = findSchedule.getMemberScheduleList().stream()
                .map(memberSchedule -> memberSchedule.getMember().getId())
                .collect(Collectors.toList());

        // ResponseDetailSchedule 객체 반환
        return ResponseDetailSchedule.builder()
                .start(findSchedule.getStart())
                .end(findSchedule.getEnd())
                .title(findSchedule.getTitle())
                .place(findSchedule.getPlace())
                .memo(findSchedule.getMemo())
                .friendsIdListDTO(friendsIdList)
                .build();
    }
}