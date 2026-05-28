package com.alby.config;

import com.alby.interest.repository.InterestRepository;
import com.alby.model.Interest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final InterestRepository interestRepository;

    public DataInitializer(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<String> defaultInterests = List.of(
                "Программирование",
                "Кино",
                "Спорт",
                "Музыка",
                "Путешествия",
                "Книги",
                "Игры",
                "Фотография",
                "Кулинария",
                "Искусство"
        );

        for (String name : defaultInterests) {
            if (!interestRepository.existsByName(name)) {
                Interest interest = new Interest();
                interest.setName(name);
                interestRepository.save(interest);
            }
        }
    }
}