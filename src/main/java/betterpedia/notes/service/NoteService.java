package betterpedia.notes.service;

import betterpedia.notes.entity.Note;
import betterpedia.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
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

        // security - yein
        validateNoteInput(pageUrl, highlightedText, noteContent, highlightColor);

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

    // security - yein (validation method)
    private void validateNoteInput(String pageUrl, String highlightedText, String noteContent, String highlightColor) {
        // URL validation
        if (pageUrl == null || pageUrl.trim().isEmpty() || pageUrl.length() > 500) {
            throw new IllegalArgumentException("Invalid page URL");
        }

        // Highlighted text validation
        if (highlightedText == null || highlightedText.trim().isEmpty() || highlightedText.length() > 1000) {
            throw new IllegalArgumentException("Invalid highlighted text");
        }

        // Note content validation
        if (noteContent != null && noteContent.length() > 2000) {
            throw new IllegalArgumentException("Note content too long (max 2000 characters)");
        }

        // Color validation
        List<String> validColors = Arrays.asList("yellow", "blue", "green", "red", "purple");
        if (highlightColor != null && !validColors.contains(highlightColor)) {
            throw new IllegalArgumentException("Invalid highlight color");
        }
    }

    // security - yein (xxs prevention)
    private String sanitizeText(String input) {
        if (input == null) return null;

        // 기본적인 HTML 태그 제거
        return input.replaceAll("<[^>]*>", "")
                .replaceAll("javascript:", "")
                .replaceAll("onload=", "")
                .replaceAll("onerror=", "")
                .trim();
    }
    private String sanitizeUrl(String url) {
        if (url == null) return null;

        // 기본적인 URL 정리 (wiki 페이지만 허용)
        if (!url.startsWith("/wiki/") && !url.equals("/wiki")) {
            throw new IllegalArgumentException("Invalid page URL - only wiki pages allowed");
        }

        return url.trim();
    }
}
