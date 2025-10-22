package betterpedia.wiki;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface documentRepository extends JpaRepository<documentEntity, Long> {
    Optional<documentEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
}