package com.alby.user.repository;

import com.alby.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);

    // Загрузить пользователя вместе с его интересами
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userInterests WHERE u.id = :userId")
    Optional<User> findByIdWithInterests(@Param("userId") UUID userId);

    // Загрузить всех пользователей, кроме заданного, вместе с их интересами
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.userInterests ui " +
            "LEFT JOIN FETCH ui.interest " +
            "WHERE u.id <> :userId")
    List<User> findAllExceptWithInterests(@Param("userId") UUID userId);
}