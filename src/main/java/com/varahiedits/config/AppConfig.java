package com.varahiedits.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.varahiedits.model.AdminUser;
import com.varahiedits.repository.AdminUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@RequiredArgsConstructor								
@Slf4j
public class AppConfig {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    
    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;	

    /**
     * Seeds default admin user on startup if not already present
     */
//    @Bean
//    public CommandLineRunner seedAdminUser() {
//        return args -> {
//            if (adminUserRepository.findByUsername(adminUsername).isEmpty()) {
//                AdminUser admin = AdminUser.builder()
//                        .username(adminUsername)
//                        .password(passwordEncoder.encode(adminPassword))
//                        .role("ADMIN")
//                        .build();
//                adminUserRepository.save(admin);
//                log.info("✅ Default admin user created: username={}", adminUsername);
//            } else {
//                log.info("✅ Admin user already exists: {}", adminUsername);
//            }
//        };
//    }
    
//    method
    
    @Bean
    public CommandLineRunner seedAdminUser() {
        return args -> {
            try {
                Thread.sleep(5000); // ⏳ wait 5 seconds for table creation
            } catch (InterruptedException e) {
            	
                e.printStackTrace();
            }

            if (adminUserRepository.findByUsername(adminUsername).isEmpty()) {
                AdminUser admin = AdminUser.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .role("ADMIN")
                        .build();
                adminUserRepository.save(admin);
                log.info("✅ Default admin user created: username={}", adminUsername);
            } else {
                log.info("✅ Admin user already exists: {}", adminUsername);
            }
        };
    }
}
