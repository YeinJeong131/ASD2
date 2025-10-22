package betterpedia.appearance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import betterpedia.appearance.entity.UserSettings;

import java.util.Optional;
import betterpedia.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
//    Optional<UserSettings> findByUserId(Long userId); // restrict checking null
//    boolean existsByUserId(Long userId);

    Optional<UserSettings> findByUser(User user);
//    boolean existsByUser(User user);

    @Query("SELECT us FROM UserSettings us WHERE us.user.id = :userId")
    Optional<UserSettings> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(us) > 0 FROM UserSettings us WHERE us.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}


