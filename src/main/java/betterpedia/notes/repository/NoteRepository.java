package betterpedia.notes.repository;

import betterpedia.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import betterpedia.user.entity.User;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserOrderByCreatedDateDesc(User user);

    List<Note> findByUserAndPageUrlOrderByCreatedDateDesc(User user, String pageUrl);

    Optional<Note> findByNoteIdAndUser(Long noteId, User user);
}
