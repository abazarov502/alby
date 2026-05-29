package com.alby.matching.service;

import com.alby.matching.dto.MatchDto;
import com.alby.model.Interest;
import com.alby.model.User;
import com.alby.model.UserInterest;
import com.alby.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MatchingService matchingService;

    private User currentUser;
    private User otherUser1;
    private User otherUser2;
    private Interest programming;
    private Interest music;
    private Interest sport;

    @BeforeEach
    void setUp() {
        // Создаём интересы
        programming = new Interest(1, "Programming");
        music = new Interest(2, "Music");
        sport = new Interest(3, "Sport");

        UUID currentUserId = UUID.randomUUID();
        UUID other1Id = UUID.randomUUID();
        UUID other2Id = UUID.randomUUID();

        // Текущий пользователь: Programming(10), Music(5)
        currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setName("Current");
        currentUser.setUserInterests(new HashSet<>(Set.of(
                createUserInterest(currentUser, programming, 10),
                createUserInterest(currentUser, music, 5)
        )));

        // Другой 1: Programming(8), Music(7), Sport(9) → score = min(10,8) + min(5,7) = 13
        otherUser1 = new User();
        otherUser1.setId(other1Id);
        otherUser1.setName("Alice");
        otherUser1.setUserInterests(new HashSet<>(Set.of(
                createUserInterest(otherUser1, programming, 8),
                createUserInterest(otherUser1, music, 7),
                createUserInterest(otherUser1, sport, 9)
        )));

        // Другой 2: Programming(3), Sport(6) → score = min(10,3) = 3
        otherUser2 = new User();
        otherUser2.setId(other2Id);
        otherUser2.setName("Bob");
        otherUser2.setUserInterests(new HashSet<>(Set.of(
                createUserInterest(otherUser2, programming, 3),
                createUserInterest(otherUser2, sport, 6)
        )));
    }

    private UserInterest createUserInterest(User user, Interest interest, int weight) {
        UserInterest ui = new UserInterest(user, interest, weight);
        return ui;
    }

    @Test
    void shouldReturnMatchesSortedByScoreDescending() {
        // Given
        UUID currentId = currentUser.getId();
        when(userRepository.findByIdWithInterests(currentId))
                .thenReturn(Optional.of(currentUser));
        when(userRepository.findAllExceptWithInterests(currentId))
                .thenReturn(List.of(otherUser1, otherUser2));

        // When
        List<MatchDto> matches = matchingService.findMatches(currentId, 10);

        // Then
        assertEquals(2, matches.size());

        // Первым должен быть Alice (score = 13), потом Bob (score = 3)
        MatchDto first = matches.get(0);
        assertEquals("Alice", first.getName());
        assertEquals(13.0, first.getScore());

        MatchDto second = matches.get(1);
        assertEquals("Bob", second.getName());
        assertEquals(3.0, second.getScore());
    }

    @Test
    void shouldRespectLimit() {
        // Given
        UUID currentId = currentUser.getId();
        when(userRepository.findByIdWithInterests(currentId))
                .thenReturn(Optional.of(currentUser));
        when(userRepository.findAllExceptWithInterests(currentId))
                .thenReturn(List.of(otherUser1, otherUser2));

        // When - запрашиваем только 1 результат
        List<MatchDto> matches = matchingService.findMatches(currentId, 1);

        // Then - должен быть только один, самый лучший
        assertEquals(1, matches.size());
        assertEquals("Alice", matches.get(0).getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonInterests() {
        // Given
        Interest none = new Interest(99, "None");

        User noMatchUser = new User();
        noMatchUser.setId(UUID.randomUUID());
        noMatchUser.setName("Stranger");
        noMatchUser.setUserInterests(new HashSet<>(Set.of(
                createUserInterest(noMatchUser, none, 5)
        )));

        UUID currentId = currentUser.getId();
        when(userRepository.findByIdWithInterests(currentId))
                .thenReturn(Optional.of(currentUser));
        when(userRepository.findAllExceptWithInterests(currentId))
                .thenReturn(List.of(noMatchUser));

        // When
        List<MatchDto> matches = matchingService.findMatches(currentId, 10);

        // Then
        assertTrue(matches.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findByIdWithInterests(nonExistentId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            matchingService.findMatches(nonExistentId, 10);
        });
    }
}