package betterpedia.notes.service;

import betterpedia.notes.entity.Note;
import betterpedia.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    public Note saveNote(Long userId, String pageUrl, String highlightedText, String noteContent, String position, String highlightColor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Note note = new Note();
        note.setUser(user);
        note.setPageUrl(pageUrl);
        note.setHighlightedText(highlightedText);
        note.setNoteContent(noteContent);
        note.setPosition(position);
        note.setHighlightColour(highlightColor != null ? highlightColor : "yellow");

        return noteRepository.save(note);
    }

    public List<Note> getAllNotesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return noteRepository.findByUserOrderByCreatedDateDesc(user);
    }

    public List<Note> getNotesByUserAndPage(Long userId, String pageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return noteRepository.findByUserAndPageUrlOrderByCreatedDateDesc(user, pageUrl);
    }

    public Note updateNote(Long userId, Long noteId, String noteContent, String highlightColor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Optional<Note> optionalNote = noteRepository.findByNoteIdAndUser(noteId, user);

        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            if (noteContent != null) {
                note.setNoteContent(noteContent);
            }
            if (highlightColor != null) {
                note.setHighlightColour(highlightColor);
            }
            note.setUpdatedDate(LocalDateTime.now());
            return noteRepository.save(note);
        } throw new RuntimeException("Note not found or access denied");
    }

    public boolean deleteNote(Long noteId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Optional<Note> optionalNote = noteRepository.findByNoteIdAndUser(noteId, user);

        if (optionalNote.isPresent()) {
            noteRepository.delete(optionalNote.get());
            return true;
        }
        return false;
    }

}
