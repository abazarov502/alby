package com.alby.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class InterestWeight {

    @NotBlank(message = "Interest name is required")
    private String interestName;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 10, message = "Weight must be at most 10")
    private int weight;

    public String getInterestName() { return interestName; }
    public void setInterestName(String interestName) { this.interestName = interestName; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
}