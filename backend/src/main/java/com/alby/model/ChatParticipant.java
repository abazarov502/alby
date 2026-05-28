package com.alby.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chat_participants")
public class ChatParticipant {

    @EmbeddedId
    private ChatParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "joined_at")
    private Instant joinedAt;

    public ChatParticipant() {}

    public ChatParticipant(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
        this.id = new ChatParticipantId(chat.getId(), user.getId());
    }

    public ChatParticipantId getId() { return id; }
    public void setId(ChatParticipantId id) { this.id = id; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }

    @PrePersist
    void onCreate() {
        if (joinedAt == null) joinedAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Chat chat;
        private User user;

        public Builder chat(Chat chat) { this.chat = chat; return this; }
        public Builder user(User user) { this.user = user; return this; }

        public ChatParticipant build() {
            return new ChatParticipant(chat, user);
        }
    }
}