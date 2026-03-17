package com.dayworks_ltd.loyalty_engine.inventory.config;

import com.dayworks_ltd.loyalty_engine.inventory.models.DefaultProduct;
import com.dayworks_ltd.loyalty_engine.inventory.repositories.DefaultProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultProductCodeBackfill implements CommandLineRunner {

    private final DefaultProductRepository repository;
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public void run(String... args) {
        List<DefaultProduct> missing = repository.findAll()
                .stream()
                .filter(p -> p.getProductCode() == null || p.getProductCode().isBlank())
                .toList();

        if (missing.isEmpty()) return;

        for (DefaultProduct p : missing) {
            String code = generateUnique();
            p.setProductCode(code);
        }
        repository.saveAll(missing);
    }

    private String generateUnique() {
        String code;
        int attempts = 0;
        do {
            code = randomCode();
            attempts++;
        } while (repository.existsByProductCode(code) && attempts < 10);
        if (repository.existsByProductCode(code)) {
            throw new IllegalStateException("Failed to generate unique product code after attempts");
        }
        return code;
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
