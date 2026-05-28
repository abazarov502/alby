package com.alby.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_interests")
public class UserInterest {

    @EmbeddedId
    private UserInterestId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("interestId")
    @JoinColumn(name = "interest_id")
    private Interest interest;

    @Column(nullable = false)
    private int weight;

    public UserInterest() {}

    public UserInterest(User user, Interest interest, int weight) {
        this.user = user;
        this.interest = interest;
        this.id = new UserInterestId(user.getId(), interest.getId());
        this.weight = weight;
    }

    public UserInterestId getId() { return id; }
    public void setId(UserInterestId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Interest getInterest() { return interest; }
    public void setInterest(Interest interest) { this.interest = interest; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    @PrePersist @PreUpdate
    void validateWeight() {
        if (weight < 0 || weight > 10) {
            throw new IllegalArgumentException("Weight must be between 0 and 10");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private Interest interest;
        private int weight;

        public Builder user(User user) { this.user = user; return this; }
        public Builder interest(Interest interest) { this.interest = interest; return this; }
        public Builder weight(int weight) { this.weight = weight; return this; }

        public UserInterest build() {
            return new UserInterest(user, interest, weight);
        }
    }
}