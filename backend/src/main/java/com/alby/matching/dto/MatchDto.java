package com.alby.matching.dto;

import java.util.UUID;

public class MatchDto {

    private UUID userId;
    private String name;
    private double score;

    public MatchDto() {}

    public MatchDto(UUID userId, String name, double score) {
        this.userId = userId;
        this.name = name;
        this.score = score;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}