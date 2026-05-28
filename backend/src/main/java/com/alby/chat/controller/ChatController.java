package com.alby.chat.controller;

import com.alby.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Создание чата
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody Map<String, UUID> request) {
        try {
            UUID user1Id = request.get("user1Id");
            UUID user2Id = request.get("user2Id");
            UUID chatId = chatService.createChat(user1Id, user2Id);
            return ResponseEntity.ok(Map.of("chatId", chatId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получение всех чатов пользователя
    @GetMapping
    public ResponseEntity<?> getUserChats(@RequestParam UUID userId) {
        try {
            List<Map<String, Object>> chats = chatService.getUserChats(userId);
            return ResponseEntity.ok(chats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получение сообщений чата
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable UUID chatId) {
        try {
            List<Map<String, Object>> messages = chatService.getMessages(chatId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Отправка сообщения
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable UUID chatId,
                                         @RequestBody Map<String, Object> request) {
        try {
            UUID senderId = UUID.fromString((String) request.get("senderId"));
            String content = (String) request.get("content");

            Map<String, Object> message = chatService.sendMessage(chatId, senderId, content);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}