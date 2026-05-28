package com.alby.chat.service;

import com.alby.chat.repository.ChatParticipantRepository;
import com.alby.chat.repository.ChatRepository;
import com.alby.chat.repository.MessageRepository;
import com.alby.model.Chat;
import com.alby.model.ChatParticipant;
import com.alby.model.Message;
import com.alby.model.User;
import com.alby.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ChatService(ChatRepository chatRepository,
                       ChatParticipantRepository participantRepository,
                       UserRepository userRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public UUID createChat(UUID user1Id, UUID user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user1Id));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user2Id));

        Chat chat = new Chat();
        chat = chatRepository.save(chat);

        ChatParticipant cp1 = new ChatParticipant(chat, user1);
        ChatParticipant cp2 = new ChatParticipant(chat, user2);
        participantRepository.save(cp1);
        participantRepository.save(cp2);

        return chat.getId();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserChats(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return user.getChatParticipants().stream()
                .map(ChatParticipant::getChat)
                .distinct()
                .map(chat -> {
                    Map<String, Object> chatInfo = new HashMap<>();
                    chatInfo.put("chatId", chat.getId());
                    // Найдём собеседника (не текущего пользователя)
                    chat.getParticipants().stream()
                            .filter(cp -> !cp.getUser().getId().equals(userId))
                            .findFirst()
                            .ifPresent(cp -> {
                                chatInfo.put("partnerName", cp.getUser().getName());
                                chatInfo.put("partnerId", cp.getUser().getId());
                            });
                    return chatInfo;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessages(UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));

        return messageRepository.findByChatIdOrderBySentAtAsc(chatId).stream()
                .map(msg -> {
                    Map<String, Object> msgInfo = new HashMap<>();
                    msgInfo.put("id", msg.getId());
                    msgInfo.put("senderId", msg.getSender().getId());
                    msgInfo.put("senderName", msg.getSender().getName());
                    msgInfo.put("content", msg.getContent());
                    msgInfo.put("sentAt", msg.getSentAt().toString());
                    return msgInfo;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> sendMessage(UUID chatId, UUID senderId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + senderId));

        Message message = new Message(chat, sender, content);
        message = messageRepository.save(message);

        Map<String, Object> msgInfo = new HashMap<>();
        msgInfo.put("id", message.getId());
        msgInfo.put("senderId", sender.getId());
        msgInfo.put("senderName", sender.getName());
        msgInfo.put("content", content);
        msgInfo.put("sentAt", message.getSentAt().toString());
        return msgInfo;
    }
}