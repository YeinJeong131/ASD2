package betterpedia.notes.service;

import betterpedia.notes.entity.Note;
import betterpedia.notes.repository.NoteRepository;
import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Note Service Tests")
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteService noteService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Save note - Success")
    void testSaveNote_Success() {
        // Given
        Long userId = 1L;
        String pageUrl = "/wiki/Spring";
        String highlightedText = "Spring Framework is...";
        String noteContent = "Important concept!";

        Note note = new Note(testUser, pageUrl, highlightedText, noteContent, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        // When
        Note saved = noteService.saveNote(userId, pageUrl, highlightedText, noteContent, null, "yellow");

        // Then
        assertNotNull(saved);
        assertEquals(pageUrl, saved.getPageUrl());
        assertEquals(highlightedText, saved.getHighlightedText());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    @DisplayName("Get all notes by user - Success")
    void testGetAllNotesByUser_Success() {
        // Given
        Long userId = 1L;
        Note note1 = new Note(testUser, "/wiki/Java", "Java text", "Note 1", null);
        Note note2 = new Note(testUser, "/wiki/Spring", "Spring text", "Note 2", null);

        List<Note> notes = Arrays.asList(note1, note2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByUserOrderByCreatedDateDesc(testUser)).thenReturn(notes);

        // When
        List<Note> found = noteService.getAllNotesByUser(userId);

        // Then
        assertNotNull(found);
        assertEquals(2, found.size());
    }

    @Test
    @DisplayName("Get notes by user and page - Success")
    void testGetNotesByUserAndPage_Success() {
        // Given
        Long userId = 1L;
        String pageUrl = "/wiki/Spring";

        Note note = new Note(testUser, pageUrl, "Spring text", "Spring note", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByUserAndPageUrlOrderByCreatedDateDesc(testUser, pageUrl))
                .thenReturn(Arrays.asList(note));

        // When
        List<Note> found = noteService.getNotesByUserAndPage(userId, pageUrl);

        // Then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(pageUrl, found.get(0).getPageUrl());
    }

    @Test
    @DisplayName("Update note - Success")
    void testUpdateNote_Success() {
        // Given
        Long noteId = 1L;
        Long userId = 1L;
        Note existingNote = new Note(testUser, "/wiki/Java", "Old text", "Old content", null);
        existingNote.setNoteId(noteId);

        String newContent = "New content";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(any(Note.class))).thenReturn(existingNote);

        // When
        Note updated = noteService.updateNote(userId, noteId, newContent, null);

        // Then
        assertNotNull(updated);
        assertEquals(newContent, updated.getNoteContent());
        verify(noteRepository, times(1)).save(existingNote);
    }

    @Test
    @DisplayName("Delete note - Success")
    void testDeleteNote_Success() {
        // Given
        Long noteId = 1L;
        Long userId = 1L;
        Note note = new Note(testUser, "/wiki/Java", "Text", "Content", null);
        note.setNoteId(noteId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.of(note));
        doNothing().when(noteRepository).delete(note);

        // When
        boolean deleted = noteService.deleteNote(noteId, userId);

        // Then
        assertTrue(deleted);
        verify(noteRepository, times(1)).delete(note);
    }

    @Test
    @DisplayName("Delete note - Not found")
    void testDeleteNote_NotFound() {
        // Given
        Long noteId = 999L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.empty());

        // When
        boolean deleted = noteService.deleteNote(noteId, userId);

        // Then
        assertFalse(deleted);
        verify(noteRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Save note - User not found")
    void testSaveNote_UserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            noteService.saveNote(userId, "/wiki/Java", "text", "content", null, "yellow");
        });
    }
}