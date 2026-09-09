package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface UsageLogRepository extends JpaRepository<UsageLog, UUID> {
    Optional<UsageLog> findByUserIdAndDate(UUID userId, LocalDate today);
}
