package com.alby.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserInterestId implements Serializable {
    private UUID userId;
    private Integer interestId;

    public UserInterestId() {}

    public UserInterestId(UUID userId, Integer interestId) {
        this.userId = userId;
        this.interestId = interestId;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Integer getInterestId() { return interestId; }
    public void setInterestId(Integer interestId) { this.interestId = interestId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInterestId)) return false;
        UserInterestId that = (UserInterestId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(interestId, that.interestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, interestId);
    }
}