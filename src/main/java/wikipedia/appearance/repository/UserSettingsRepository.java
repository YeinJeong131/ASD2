package wikipedia.appearance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wikipedia.appearance.entity.UserSettings;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByName(String name);
    boolean existsByName(String name);
}


