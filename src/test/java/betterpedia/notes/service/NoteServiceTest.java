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
import java.util.Collections;
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
    @DisplayName("Save note - Success with all parameters")
    void testSaveNote_Success() {
        // Given
        Long userId = 1L;
        String pageUrl = "/wiki/Spring";
        String highlightedText = "Spring Framework is a comprehensive programming model";
        String noteContent = "Important concept to remember for the exam!";
        String highlightColor = "yellow";

        Note savedNote = new Note(testUser, pageUrl, highlightedText, noteContent, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        // When
        Note result = noteService.saveNote(userId, pageUrl, highlightedText, noteContent, null, highlightColor);

        // Then
        assertNotNull(result);
        assertEquals(pageUrl, result.getPageUrl());
        assertEquals(highlightedText, result.getHighlightedText());
        assertEquals(noteContent, result.getNoteContent());
        assertEquals(testUser, result.getUser());
        verify(noteRepository, times(1)).save(any(Note.class));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Save note - User not found throws exception")
    void testSaveNote_UserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            noteService.saveNote(userId, "/wiki/Java", "text", "content", null, "yellow");
        });

        verify(noteRepository, never()).save(any());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Get all notes by user - Success with multiple notes")
    void testGetAllNotesByUser_Success() {
        // Given
        Long userId = 1L;
        Note note1 = new Note(testUser, "/wiki/Java", "Java programming language", "Java is object-oriented", null);
        Note note2 = new Note(testUser, "/wiki/Spring", "Spring Framework", "Dependency injection framework", null);
        Note note3 = new Note(testUser, "/wiki/Database", "Database concepts", "ACID properties", null);

        List<Note> notes = Arrays.asList(note1, note2, note3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByUserOrderByCreatedDateDesc(testUser)).thenReturn(notes);

        // When
        List<Note> result = noteService.getAllNotesByUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("/wiki/Java", result.get(0).getPageUrl());
        assertEquals("/wiki/Spring", result.get(1).getPageUrl());
        assertEquals("/wiki/Database", result.get(2).getPageUrl());
        verify(userRepository, times(1)).findById(userId);
        verify(noteRepository, times(1)).findByUserOrderByCreatedDateDesc(testUser);
    }

    @Test
    @DisplayName("Get all notes by user - Empty list when no notes exist")
    void testGetAllNotesByUser_EmptyList() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByUserOrderByCreatedDateDesc(testUser)).thenReturn(Collections.emptyList());

        // When
        List<Note> result = noteService.getAllNotesByUser(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(userId);
        verify(noteRepository, times(1)).findByUserOrderByCreatedDateDesc(testUser);
    }

    @Test
    @DisplayName("Get notes by user and page - Success")
    void testGetNotesByUserAndPage_Success() {
        // Given
        Long userId = 1L;
        String pageUrl = "/wiki/Spring";

        Note note1 = new Note(testUser, pageUrl, "Spring IoC", "Inversion of Control", null);
        Note note2 = new Note(testUser, pageUrl, "Spring AOP", "Aspect Oriented Programming", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByUserAndPageUrlOrderByCreatedDateDesc(testUser, pageUrl))
                .thenReturn(Arrays.asList(note1, note2));

        // When
        List<Note> result = noteService.getNotesByUserAndPage(userId, pageUrl);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(pageUrl, result.get(0).getPageUrl());
        assertEquals(pageUrl, result.get(1).getPageUrl());
        verify(userRepository, times(1)).findById(userId);
        verify(noteRepository, times(1)).findByUserAndPageUrlOrderByCreatedDateDesc(testUser, pageUrl);
    }

    @Test
    @DisplayName("Get notes by user and page - User not found throws exception")
    void testGetNotesByUserAndPage_UserNotFound() {
        // Given
        Long userId = 999L;
        String pageUrl = "/wiki/Spring";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            noteService.getNotesByUserAndPage(userId, pageUrl);
        });

        verify(noteRepository, never()).findByUserAndPageUrlOrderByCreatedDateDesc(any(), any());
    }

    @Test
    @DisplayName("Update note - Success with new content")
    void testUpdateNote_Success() {
        // Given
        Long noteId = 1L;
        Long userId = 1L;
        Note existingNote = new Note(testUser, "/wiki/Java", "Java basics", "Old content about Java", null);
        existingNote.setNoteId(noteId);

        String newContent = "Updated content with more detailed Java information";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        // When
        Note result = noteService.updateNote(userId, noteId, newContent, null);

        // Then
        assertNotNull(result);
        assertEquals(newContent, result.getNoteContent());
        assertEquals(noteId, result.getNoteId());
        assertEquals("/wiki/Java", result.getPageUrl());
        verify(noteRepository, times(1)).save(existingNote);
        verify(noteRepository, times(1)).findByNoteIdAndUser(noteId, testUser);
    }

    @Test
    @DisplayName("Update note - Note not found throws exception")
    void testUpdateNote_NoteNotFound() {
        // Given
        Long noteId = 999L;
        Long userId = 1L;
        String newContent = "New content";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            noteService.updateNote(userId, noteId, newContent, null);
        });

        verify(noteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete note - Success")
    void testDeleteNote_Success() {
        // Given
        Long noteId = 1L;
        Long userId = 1L;
        Note note = new Note(testUser, "/wiki/Java", "Java text", "Java content", null);
        note.setNoteId(noteId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.of(note));
        doNothing().when(noteRepository).delete(note);

        // When
        boolean result = noteService.deleteNote(noteId, userId);

        // Then
        assertTrue(result);
        verify(noteRepository, times(1)).delete(note);
        verify(noteRepository, times(1)).findByNoteIdAndUser(noteId, testUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Delete note - Note not found returns false")
    void testDeleteNote_NotFound() {
        // Given
        Long noteId = 999L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.empty());

        // When
        boolean result = noteService.deleteNote(noteId, userId);

        // Then
        assertFalse(result);
        verify(noteRepository, never()).delete(any());
        verify(noteRepository, times(1)).findByNoteIdAndUser(noteId, testUser);
    }

    @Test
    @DisplayName("Delete note - User not found throws exception")
    void testDeleteNote_UserNotFound() {
        // Given
        Long noteId = 1L;
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            noteService.deleteNote(noteId, userId);
        });

        verify(noteRepository, never()).findByNoteIdAndUser(any(), any());
        verify(noteRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Save note - Success with minimal required data")
    void testSaveNote_MinimalData() {
        // Given
        Long userId = 1L;
        String pageUrl = "/wiki/Test";
        String highlightedText = "Test text";
        String noteContent = null; // No note content
        String highlightColor = null; // No color specified

        Note savedNote = new Note(testUser, pageUrl, highlightedText, noteContent, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        // When
        Note result = noteService.saveNote(userId, pageUrl, highlightedText, noteContent, null, highlightColor);

        // Then
        assertNotNull(result);
        assertEquals(pageUrl, result.getPageUrl());
        assertEquals(highlightedText, result.getHighlightedText());
        assertNull(result.getNoteContent());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    @DisplayName("Update note - Success with highlight color update")
    void testUpdateNote_WithHighlightColor() {
        // Given
        Long noteId = 1L;
        Long userId = 1L;
        Note existingNote = new Note(testUser, "/wiki/CSS", "CSS styling", "Original note", null);
        existingNote.setNoteId(noteId);

        String newContent = "Updated CSS note";
        String newHighlightColor = "blue";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(noteRepository.findByNoteIdAndUser(noteId, testUser)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        // When
        Note result = noteService.updateNote(userId, noteId, newContent, newHighlightColor);

        // Then
        assertNotNull(result);
        assertEquals(newContent, result.getNoteContent());
        verify(noteRepository, times(1)).save(existingNote);
    }
}