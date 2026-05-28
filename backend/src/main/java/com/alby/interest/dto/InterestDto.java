package com.alby.interest.dto;

public class InterestDto {

    private Integer id;
    private String name;

    public InterestDto() {}

    public InterestDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}