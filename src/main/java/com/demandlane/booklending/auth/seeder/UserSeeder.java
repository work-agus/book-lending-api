package com.demandlane.booklending.auth.seeder;

import com.demandlane.booklending.auth.auth.UserRepository;
import com.demandlane.booklending.auth.model.User;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            UUID uuid7User1 = UuidCreator.getTimeOrderedEpoch();
            User user1 = new User();
            user1.setId(uuid7User1);
            user1.setName("Admin");
            user1.setUsername("admin");
            user1.setPassword(passwordEncoder.encode("admin123"));
            user1.setEmail("admin@example.com");
            user1.setRole("ADMIN");

            UUID uuid7User2 = UuidCreator.getTimeOrderedEpoch();
            User user2 = new User();
            user2.setId(uuid7User2);
            user2.setName("Member");
            user2.setUsername("johndoe");
            user2.setPassword(passwordEncoder.encode("password123"));
            user2.setEmail("john@example.com");
            user2.setRole("MEMBER");

            userRepository.saveAll(List.of(user1, user2));
            System.out.println("Database seeded with default users.");
        }
    }
}