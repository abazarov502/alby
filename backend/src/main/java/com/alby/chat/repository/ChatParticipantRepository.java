package com.alby.chat.repository;

import com.alby.model.ChatParticipant;
import com.alby.model.ChatParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
}