package com.alby.interest.service;

import com.alby.interest.dto.InterestDto;
import com.alby.interest.repository.InterestRepository;
import com.alby.model.Interest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterestService {

    private final InterestRepository interestRepository;

    public InterestService(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    public List<InterestDto> getAllInterests() {
        List<Interest> interests = interestRepository.findAll();

        return interests.stream()
                .map(interest -> new InterestDto(interest.getId(), interest.getName()))
                .collect(Collectors.toList());
    }
}