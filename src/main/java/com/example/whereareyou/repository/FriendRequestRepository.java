package com.example.whereareyou.repository;

import com.example.whereareyou.domain.FriendRequest;
import com.example.whereareyou.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest,String > {

    List<FriendRequest> findByReceiverId(Member receiverId);
    List<FriendRequest> findBySenderId(Member senderId);
}
