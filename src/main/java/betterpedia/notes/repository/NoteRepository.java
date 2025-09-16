package betterpedia.notes.repository;

import betterpedia.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserIdOrderByCreatedDateDesc(Long userId);

    List<Note> findByUserIdAndPageUrlOrderByCreatedDateDesc(Long userId, String pageUrl);

    Optional<Note> findByNoteIdAndUserId(Long noteId, Long userId);
}
