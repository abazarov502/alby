package com.alby.matching.controller;

import com.alby.matching.dto.MatchDto;
import com.alby.matching.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "http://localhost:5173")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping
    public ResponseEntity<?> getMatches(@RequestParam UUID userId,
                                        @RequestParam(defaultValue = "10") int limit) {
        try {
            List<MatchDto> matches = matchingService.findMatches(userId, limit);
            return ResponseEntity.ok(matches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}