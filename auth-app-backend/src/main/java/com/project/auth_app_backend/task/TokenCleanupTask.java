package com.project.auth_app_backend.task;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.auth_app_backend.repositories.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupTask {

    private final RefreshTokenRepository refreshTokenRepository;

    // Runs automatically every day at midnight (Server time)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void purgeExpiredTokens() {
        log.info("Scheduled Task: Beginning database purge of expired refresh tokens...");
        
        // Delete anything where the expiration time has officially passed
        long deletedCount = refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        
        log.info("Scheduled Task: Purge complete. Swept away {} dead token records.", deletedCount);
    }
}
