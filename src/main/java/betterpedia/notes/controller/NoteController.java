package betterpedia.notes.controller;

import betterpedia.notes.entity.Note;
import betterpedia.notes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    /* save new notes */
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestParam String pageUrl,
                                           @RequestParam String highlightedText,
                                           @RequestParam(required = false) String noteContent,
                                           @RequestParam(required = false) String position,
                                           @RequestParam(required = false) String highlightColor) {
        Long userId = 1L;

        Note savedNote = noteService.saveNote(userId, pageUrl, highlightedText, noteContent, position, highlightColor);

        return ResponseEntity.ok(savedNote);
    }

    /* bringing all the notes */
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        Long userId = 1L;
        List<Note> notes = noteService.getAllNotesByUser(userId);
        return ResponseEntity.ok(notes);
    }

    /* */
    @GetMapping("/page")
    public ResponseEntity<List<Note>> getNotesByPage(@RequestParam String url) {
        Long userId = 1L;
        List<Note> notes = noteService.getNotesByUserAndPage(userId, url);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable Long noteId, @RequestParam(required = false) String noteContent, @RequestParam(required = false) String highlightColor) {
        Long userId = 1L;
        Note updatedNote = noteService.updateNote(userId, noteId, noteContent, highlightColor); return ResponseEntity.ok(updatedNote);}

    @DeleteMapping("/{noteId}")
    public ResponseEntity<String> deleteNote(@PathVariable Long noteId) {
        Long userId = 1L;
        boolean deleted = noteService.deleteNote(noteId, userId);

        if (deleted) {
            return ResponseEntity.ok("Note deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}