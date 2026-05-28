package com.alby.interest.repository;

import com.alby.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Integer> {
    Optional<Interest> findByName(String name);
    boolean existsByName(String name);
}