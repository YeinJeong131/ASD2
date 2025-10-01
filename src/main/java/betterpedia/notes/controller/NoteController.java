package betterpedia.notes.controller;

import betterpedia.notes.entity.Note;
import betterpedia.notes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

//    /* save new notes */
//    @PostMapping
//    public ResponseEntity<Note> createNote(@RequestParam String pageUrl,
//                                           @RequestParam String highlightedText,
//                                           @RequestParam(required = false) String noteContent,
//                                           @RequestParam(required = false) String position,
//                                           @RequestParam(required = false) String highlightColor) {
//        Long userId = 1L;
//
//        Note savedNote = noteService.saveNote(userId, pageUrl, highlightedText, noteContent, position, highlightColor);
//
//        return ResponseEntity.ok(savedNote);
//    }
    @PostMapping
    public ResponseEntity<?> createNote(@RequestParam String pageUrl,
                                        @RequestParam String highlightedText,
                                        @RequestParam(required = false) String noteContent,
                                        @RequestParam(required = false) String position,
                                        @RequestParam(required = false) String highlightColor,
                                        HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            Note savedNote = noteService.saveNote(userId, pageUrl, highlightedText,
                    noteContent, position, highlightColor);
            return ResponseEntity.ok(savedNote);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to create note: " + e.getMessage());
        }
    }


//    /* bringing all the notes */
//    @GetMapping
//    public ResponseEntity<List<Note>> getAllNotes() {
//        Long userId = 1L;
//        List<Note> notes = noteService.getAllNotesByUser(userId);
//        return ResponseEntity.ok(notes);
//    }

    @GetMapping
    public ResponseEntity<?> getAllNotes(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            List<Note> notes = noteService.getAllNotesByUser(userId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get notes: " + e.getMessage());
        }
    }


//    /* */
//    @GetMapping("/page")
//    public ResponseEntity<List<Note>> getNotesByPage(@RequestParam String url) {
//        Long userId = 1L;
//        List<Note> notes = noteService.getNotesByUserAndPage(userId, url);
//        return ResponseEntity.ok(notes);
//    }
//
//    @PutMapping("/{noteId}")
//    public ResponseEntity<Note> updateNote(@PathVariable Long noteId, @RequestParam(required = false) String noteContent, @RequestParam(required = false) String highlightColor) {
//        Long userId = 1L;
//        Note updatedNote = noteService.updateNote(userId, noteId, noteContent, highlightColor); return ResponseEntity.ok(updatedNote);}
//
//    @DeleteMapping("/{noteId}")
//    public ResponseEntity<String> deleteNote(@PathVariable Long noteId) {
//        Long userId = 1L;
//        boolean deleted = noteService.deleteNote(noteId, userId);
//
//        if (deleted) {
//            return ResponseEntity.ok("Note deleted successfully");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/page")
    public ResponseEntity<?> getNotesByPage(@RequestParam String url, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            List<Note> notes = noteService.getNotesByUserAndPage(userId, url);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get notes: " + e.getMessage());
        }
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateNote(@PathVariable Long noteId,
                                        @RequestParam(required = false) String noteContent,
                                        @RequestParam(required = false) String highlightColor,
                                        HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            Note updatedNote = noteService.updateNote(userId, noteId, noteContent, highlightColor);
            return ResponseEntity.ok(updatedNote);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to update note: " + e.getMessage());
        }
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long noteId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            boolean deleted = noteService.deleteNote(noteId, userId);

            if (deleted) {
                return ResponseEntity.ok("Note deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to delete note: " + e.getMessage());
        }
    }


}