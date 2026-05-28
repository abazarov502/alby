package com.alby.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "matches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
})
public class Match {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "matched_at")
    private Instant matchedAt;

    public Match() {}

    public Match(User user1, User user2, BigDecimal score) {
        this.user1 = user1;
        this.user2 = user2;
        this.score = score;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser1() { return user1; }
    public void setUser1(User user1) { this.user1 = user1; }

    public User getUser2() { return user2; }
    public void setUser2(User user2) { this.user2 = user2; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public Instant getMatchedAt() { return matchedAt; }
    public void setMatchedAt(Instant matchedAt) { this.matchedAt = matchedAt; }

    @PrePersist
    void onCreate() {
        if (matchedAt == null) matchedAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user1;
        private User user2;
        private BigDecimal score;

        public Builder user1(User user1) { this.user1 = user1; return this; }
        public Builder user2(User user2) { this.user2 = user2; return this; }
        public Builder score(BigDecimal score) { this.score = score; return this; }

        public Match build() {
            return new Match(user1, user2, score);
        }
    }
}