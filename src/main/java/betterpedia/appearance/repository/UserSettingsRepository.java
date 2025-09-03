package betterpedia.appearance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import betterpedia.appearance.entity.UserSettings;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId); // restrict checking null
    boolean existsByUserId(Long userId);
}


