package betterpedia.appearance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import betterpedia.appearance.entity.UserSettings;

import java.util.Optional;
import betterpedia.user.entity.User;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId); // restrict checking null
    boolean existsByUserId(Long userId);

    Optional<UserSettings> findByUser(User user);
    boolean existsByUser(User user);
}


