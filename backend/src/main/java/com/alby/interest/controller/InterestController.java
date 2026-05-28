package com.alby.interest.controller;

import com.alby.interest.dto.InterestDto;
import com.alby.interest.service.InterestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
@CrossOrigin(origins = "http://localhost:5173")
public class InterestController {

    private final InterestService interestService;

    public InterestController(InterestService interestService) {
        this.interestService = interestService;
    }

    @GetMapping
    public ResponseEntity<List<InterestDto>> getAllInterests() {
        List<InterestDto> interests = interestService.getAllInterests();
        return ResponseEntity.ok(interests);
    }
}