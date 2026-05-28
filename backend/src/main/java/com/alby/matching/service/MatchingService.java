package com.alby.matching.service;

import com.alby.matching.dto.MatchDto;
import com.alby.model.User;
import com.alby.model.UserInterest;
import com.alby.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private final UserRepository userRepository;

    public MatchingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchDto> findMatches(UUID userId, int limit) {
        // 1. Загружаем текущего пользователя с его интересами
        User currentUser = userRepository.findByIdWithInterests(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. Строим мапу: ID интереса → вес для текущего пользователя
        Map<Integer, Integer> currentWeights = new HashMap<>();
        for (UserInterest ui : currentUser.getUserInterests()) {
            currentWeights.put(ui.getInterest().getId(), ui.getWeight());
        }

        // 3. Загружаем всех остальных пользователей с интересами
        List<User> otherUsers = userRepository.findAllExceptWithInterests(userId);

        // 4. Считаем скор для каждого другого пользователя
        List<MatchDto> matches = new ArrayList<>();
        for (User other : otherUsers) {
            double score = 0.0;
            for (UserInterest oi : other.getUserInterests()) {
                Integer myWeight = currentWeights.get(oi.getInterest().getId());
                if (myWeight != null) {
                    score += Math.min(myWeight, oi.getWeight());
                }
            }

            if (score > 0) { // показываем только тех, у кого есть общие интересы
                matches.add(new MatchDto(other.getId(), other.getName(), score));
            }
        }

        // 5. Сортируем по убыванию скора и обрезаем лимит
        matches.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return matches.stream().limit(limit).collect(Collectors.toList());
    }
}