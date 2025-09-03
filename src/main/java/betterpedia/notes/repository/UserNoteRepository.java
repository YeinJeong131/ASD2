package betterpedia.notes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import betterpedia.notes.entity.UserNote;
import java.util.List;

public interface UserNoteRepository extends JpaRepository<UserNote, Long> {
    List<UserNote> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserNote> findByUserIdAndPageUrlOrderByCreatedAtAsc(Long userId, String pageUrl);
}