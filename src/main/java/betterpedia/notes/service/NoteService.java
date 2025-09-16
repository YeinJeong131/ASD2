package betterpedia.notes.service;

import betterpedia.notes.entity.Note;
import betterpedia.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public Note saveNote(Long userId, String pageUrl, String highlightedText, String noteContent, String position, String highlightColor) {
        Note note = new Note();
        note.setUserId(userId);
        note.setPageUrl(pageUrl);
        note.setHighlightedText(highlightedText);
        note.setNoteContent(noteContent);
        note.setPosition(position);
        note.setHighlightColour(highlightColor != null ? highlightColor : "yellow");

        return noteRepository.save(note);
    }

    public List<Note> getAllNotesByUser(Long userId) {
        return noteRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }

    public List<Note> getNotesByUserAndPage(Long userId, String pageUrl) {
        return noteRepository.findByUserIdAndPageUrlOrderByCreatedDateDesc(userId, pageUrl);
    }

    public Note updateNote(Long noteId, Long userId, String noteContent, String highlightColor) {
        Optional<Note> optionalNote = noteRepository.findByNoteIdAndUserId(noteId, userId);

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
        }

        throw new RuntimeException("Note not found");
    }

    public boolean deleteNote(Long noteId, Long userId) {
        Optional<Note> optionalNote = noteRepository.findByNoteIdAndUserId(noteId, userId);

        if (optionalNote.isPresent()) {
            noteRepository.delete(optionalNote.get());
            return true;
        }
        return false;
    }

}
