package com.demandlane.booklending.auth.seeder;

import com.demandlane.booklending.auth.auth.UserRepository;
import com.demandlane.booklending.auth.model.User;
import com.demandlane.booklending.auth.service.AuthService;
import com.demandlane.booklending.common.util.Constants;
import com.demandlane.booklending.common.util.Utils;
import com.github.f4b6a3.uuid.UuidCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Starting user seeding process...");

        if (userRepository.count() == 0) {
            User user0 = new User();
            user0.setId(Utils.getSystemUUID());
            user0.setName("System");
            user0.setUsername("system");
            user0.setPassword(passwordEncoder.encode("system123"));
            user0.setEmail("system@example.com");
            user0.setRole("SYSTEM");

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

            UUID uuid7User3 = UuidCreator.getTimeOrderedEpoch();
            User user3 = new User();
            user3.setId(uuid7User3);
            user3.setName("Librarian");
            user3.setUsername("librarian");
            user3.setPassword(passwordEncoder.encode("password123"));
            user3.setEmail("library@example.com");
            user3.setRole("LIBRARIAN");

            userRepository.saveAll(List.of(user0, user1, user2, user3));
            LOGGER.info("User seeding completed successfully. {} users created.", userRepository.count());
        }
    }
}