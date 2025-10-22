package betterpedia.appearance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import betterpedia.appearance.entity.UserSettings;
import betterpedia.user.entity.User;
import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUser(User user);
    boolean existsByUser(User user);
}