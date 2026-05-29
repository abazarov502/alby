package com.alby.user.service;

import com.alby.interest.repository.InterestRepository;
import com.alby.model.Interest;
import com.alby.model.User;
import com.alby.user.dto.InterestWeight;
import com.alby.user.dto.RegisterRequest;
import com.alby.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setLogin("testuser");
        request.setPassword("password123");
        request.setName("Test User");
        request.setInterests(List.of(
                createInterestWeight("Programming", 10),
                createInterestWeight("Music", 7)
        ));
    }

    private InterestWeight createInterestWeight(String name, int weight) {
        InterestWeight iw = new InterestWeight();
        iw.setInterestName(name);
        iw.setWeight(weight);
        return iw;
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given (Подготовка)
        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        Interest programming = new Interest(1, "Programming");
        Interest music = new Interest(2, "Music");
        when(interestRepository.findByName("Programming")).thenReturn(Optional.of(programming));
        when(interestRepository.findByName("Music")).thenReturn(Optional.of(music));

        User savedUser = new User();
        savedUser.setId(java.util.UUID.randomUUID());
        savedUser.setLogin("testuser");
        savedUser.setName("Test User");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When (Действие)
        User result = userService.register(request);

        // Then (Проверка)
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals("Test User", result.getName());

        verify(userRepository).existsByLogin("testuser");
        verify(passwordEncoder).encode("password123");
        verify(interestRepository).findByName("Programming");
        verify(interestRepository).findByName("Music");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenLoginExists() {
        // Given
        when(userRepository.existsByLogin("testuser")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );

        assertEquals("Login already exists: testuser", exception.getMessage());
        verify(userRepository).existsByLogin("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldHashPasswordBeforeSaving() {
        // Given
        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        Interest programming = new Interest(1, "Programming");
        Interest music = new Interest(2, "Music");  // ← добавляем второй интерес
        when(interestRepository.findByName("Programming")).thenReturn(Optional.of(programming));
        when(interestRepository.findByName("Music")).thenReturn(Optional.of(music));  // ← мокаем его

        User savedUser = new User();
        savedUser.setLogin("testuser");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        userService.register(request);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("hashed_password", capturedUser.getPasswordHash());
        assertNotEquals("password123", capturedUser.getPasswordHash());
    }

    @Test
    void shouldCreateNewInterestIfNotFound() {
        // Given
        request.setInterests(List.of(createInterestWeight("NewInterest", 8)));

        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(interestRepository.findByName("NewInterest")).thenReturn(Optional.empty());

        Interest newInterest = new Interest(5, "NewInterest");
        when(interestRepository.save(any(Interest.class))).thenReturn(newInterest);

        User savedUser = new User();
        savedUser.setLogin("testuser");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        userService.register(request);

        // Then
        verify(interestRepository).findByName("NewInterest");
        verify(interestRepository).save(any(Interest.class));
    }
}