package com.alby.user.service;

import com.alby.model.*;
import com.alby.interest.repository.InterestRepository;
import com.alby.user.dto.InterestWeight;
import com.alby.user.dto.RegisterRequest;
import com.alby.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       InterestRepository interestRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.interestRepository = interestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        // 1. Проверить, нет ли уже такого логина
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new IllegalArgumentException("Login already exists: " + request.getLogin());
        }

        // 2. Создать пользователя
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        // createdAt проставится в @PrePersist

        // 3. Обработать интересы
        Set<UserInterest> userInterests = new HashSet<>();
        if (request.getInterests() != null) {
            for (InterestWeight iw : request.getInterests()) {
                Interest interest = interestRepository.findByName(iw.getInterestName())
                        .orElseGet(() -> {
                            Interest newInterest = new Interest();
                            newInterest.setName(iw.getInterestName());
                            return interestRepository.save(newInterest);
                        });

                UserInterest ui = new UserInterest(user, interest, iw.getWeight());
                // id установится в конструкторе
                userInterests.add(ui);
            }
        }
        user.setUserInterests(userInterests);

        // 4. Сохранить пользователя (каскад сохранит UserInterest)
        return userRepository.save(user);
    }
}