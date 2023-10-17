package com.example.whereareyou.service;

import com.example.whereareyou.domain.FriendRequest;
import com.example.whereareyou.domain.Member;
import com.example.whereareyou.dto.FriendInviteRequest;
import com.example.whereareyou.exception.customexception.UserNotFoundException;
import com.example.whereareyou.repository.FriendRepository;
import com.example.whereareyou.repository.FriendRequestRepository;
import com.example.whereareyou.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;

    public void friendRequest(FriendInviteRequest request) {
        Member member = memberRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 userId입니다.")); // sender

        List<String> requestList = request.getMemberIdList(); // 친구 요청 리스트 receiver

        // 본인 ID를 확인하고 예외 처리
        if (requestList.contains(member.getUserId())) {
            throw new RuntimeException("본인 ID는 친구 요청을 보낼 수 없습니다.");
        }

        List<Member> allById = memberRepository.findByUserIdIn(requestList); // 친구 요청 리스트 멤버 조회

        List<FriendRequest> friendRequests = allById.stream()
                .map(receiver -> FriendRequest.builder()
                        .senderId(member.getId())
                        .member(receiver)
                        .build())
                .collect(Collectors.toList());

        friendRequestRepository.saveAll(friendRequests);
    }

}
