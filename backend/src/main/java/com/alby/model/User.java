package com.alby.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 100)
    private String name;

    @Column(name = "created_at")
    private Instant createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserInterest> userInterests = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<ChatParticipant> chatParticipants = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private Set<Message> messages = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user1")
    private Set<Match> matchesAsUser1 = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user2")
    private Set<Match> matchesAsUser2 = new HashSet<>();

    // --- Конструкторы ---
    public User() {}

    public User(String login, String passwordHash, String name) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
    }

    public User(UUID id, String login, String passwordHash, String name, Instant createdAt) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
        this.createdAt = createdAt;
    }

    // --- Геттеры и сеттеры ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Set<UserInterest> getUserInterests() { return userInterests; }
    public void setUserInterests(Set<UserInterest> userInterests) { this.userInterests = userInterests; }

    public Set<ChatParticipant> getChatParticipants() { return chatParticipants; }
    public void setChatParticipants(Set<ChatParticipant> chatParticipants) { this.chatParticipants = chatParticipants; }

    public Set<Message> getMessages() { return messages; }
    public void setMessages(Set<Message> messages) { this.messages = messages; }

    public Set<Match> getMatchesAsUser1() { return matchesAsUser1; }
    public void setMatchesAsUser1(Set<Match> matchesAsUser1) { this.matchesAsUser1 = matchesAsUser1; }

    public Set<Match> getMatchesAsUser2() { return matchesAsUser2; }
    public void setMatchesAsUser2(Set<Match> matchesAsUser2) { this.matchesAsUser2 = matchesAsUser2; }

    // --- Билдер (внутренний статический класс) ---
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String login;
        private String passwordHash;
        private String name;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder login(String login) { this.login = login; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public User build() {
            return new User(id, login, passwordHash, name, createdAt);
        }
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}