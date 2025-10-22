package betterpedia.notes.controller;

import betterpedia.notes.entity.Note;
import betterpedia.notes.service.NoteService;
import betterpedia.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@DisplayName("Note Controller Tests")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    private User createTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");
        return user;
    }

    private MockHttpSession createSession(Long userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);
        return session;
    }

    @Test
    @DisplayName("POST /api/notes - Create note successfully")
    void testCreateNote_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        String pageUrl = "/wiki/Java";
        String highlightedText = "Java is a programming language";
        String noteContent = "Important concept to remember!";

        Note note = new Note(user, pageUrl, highlightedText, noteContent, null);

        when(noteService.saveNote(eq(userId), eq(pageUrl), eq(highlightedText), eq(noteContent), isNull(), anyString()))
                .thenReturn(note);

        // When & Then
        mockMvc.perform(post("/api/notes")
                        .session(session)
                        .param("pageUrl", pageUrl)
                        .param("highlightedText", highlightedText)
                        .param("noteContent", noteContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageUrl").value(pageUrl))
                .andExpect(jsonPath("$.highlightedText").value(highlightedText))
                .andExpect(jsonPath("$.noteContent").value(noteContent));

        verify(noteService, times(1)).saveNote(eq(userId), eq(pageUrl), eq(highlightedText), eq(noteContent), isNull(), anyString());
    }

    @Test
    @DisplayName("POST /api/notes - Unauthorized when not logged in")
    void testCreateNote_NotLoggedIn() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/notes")
                        .param("pageUrl", "/wiki/Java")
                        .param("highlightedText", "text")
                        .param("noteContent", "content"))
                .andExpect(status().isUnauthorized());

        verify(noteService, never()).saveNote(anyLong(), anyString(), anyString(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("GET /api/notes - Get all notes for logged in user")
    void testGetAllNotes_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        Note note1 = new Note(user, "/wiki/Java", "Java text", "Java note", null);
        Note note2 = new Note(user, "/wiki/Spring", "Spring text", "Spring note", null);

        List<Note> notes = Arrays.asList(note1, note2);
        when(noteService.getAllNotesByUser(userId)).thenReturn(notes);

        // When & Then
        mockMvc.perform(get("/api/notes")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(noteService, times(1)).getAllNotesByUser(userId);
    }

    @Test
    @DisplayName("GET /api/notes - Unauthorized when not logged in")
    void testGetAllNotes_NotLoggedIn() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isUnauthorized());

        verify(noteService, never()).getAllNotesByUser(anyLong());
    }

    @Test
    @DisplayName("GET /api/notes/page - Get notes by page URL")
    void testGetNotesByPage_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        String pageUrl = "/wiki/Java";
        Note note = new Note(user, pageUrl, "Java programming", "Important notes", null);

        when(noteService.getNotesByUserAndPage(userId, pageUrl))
                .thenReturn(Collections.singletonList(note));

        // When & Then
        mockMvc.perform(get("/api/notes/page")
                        .session(session)
                        .param("url", pageUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].pageUrl").value(pageUrl));

        verify(noteService, times(1)).getNotesByUserAndPage(userId, pageUrl);
    }

    @Test
    @DisplayName("PUT /api/notes/{noteId} - Update note successfully")
    void testUpdateNote_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        Long noteId = 1L;
        String newContent = "Updated note content";
        Note updatedNote = new Note(user, "/wiki/Java", "Java text", newContent, null);
        updatedNote.setNoteId(noteId);

        when(noteService.updateNote(eq(userId), eq(noteId), eq(newContent), isNull()))
                .thenReturn(updatedNote);

        // When & Then
        mockMvc.perform(put("/api/notes/{noteId}", noteId)
                        .session(session)
                        .param("noteContent", newContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteId").value(noteId))
                .andExpect(jsonPath("$.noteContent").value(newContent));

        verify(noteService, times(1)).updateNote(eq(userId), eq(noteId), eq(newContent), isNull());
    }

    @Test
    @DisplayName("PUT /api/notes/{noteId} - Unauthorized when not logged in")
    void testUpdateNote_NotLoggedIn() throws Exception {
        // Given
        Long noteId = 1L;
        String newContent = "Updated content";

        // When & Then
        mockMvc.perform(put("/api/notes/{noteId}", noteId)
                        .param("noteContent", newContent))
                .andExpect(status().isUnauthorized());

        verify(noteService, never()).updateNote(anyLong(), anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("DELETE /api/notes/{noteId} - Delete note successfully")
    void testDeleteNote_Success() throws Exception {
        // Given
        Long userId = 1L;
        Long noteId = 1L;
        MockHttpSession session = createSession(userId);

        when(noteService.deleteNote(noteId, userId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/notes/{noteId}", noteId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Note deleted successfully"));

        verify(noteService, times(1)).deleteNote(noteId, userId);
    }

    @Test
    @DisplayName("DELETE /api/notes/{noteId} - Note not found returns 404")
    void testDeleteNote_NotFound() throws Exception {
        // Given
        Long userId = 1L;
        Long noteId = 999L;
        MockHttpSession session = createSession(userId);

        when(noteService.deleteNote(noteId, userId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/notes/{noteId}", noteId)
                        .session(session))
                .andExpect(status().isNotFound());

        verify(noteService, times(1)).deleteNote(noteId, userId);
    }

    @Test
    @DisplayName("DELETE /api/notes/{noteId} - Unauthorized when not logged in")
    void testDeleteNote_NotLoggedIn() throws Exception {
        // Given
        Long noteId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/notes/{noteId}", noteId))
                .andExpect(status().isUnauthorized());

        verify(noteService, never()).deleteNote(anyLong(), anyLong());
    }

    @Test
    @DisplayName("POST /api/notes - Create note with highlight color")
    void testCreateNote_WithHighlightColor() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        String pageUrl = "/wiki/Spring";
        String highlightedText = "Spring Framework";
        String noteContent = "Important framework";
        String highlightColor = "yellow";

        Note note = new Note(user, pageUrl, highlightedText, noteContent, null);

        when(noteService.saveNote(eq(userId), eq(pageUrl), eq(highlightedText), eq(noteContent), isNull(), eq(highlightColor)))
                .thenReturn(note);

        // When & Then
        mockMvc.perform(post("/api/notes")
                        .session(session)
                        .param("pageUrl", pageUrl)
                        .param("highlightedText", highlightedText)
                        .param("noteContent", noteContent)
                        .param("highlightColor", highlightColor))
                .andExpect(status().isOk());

        verify(noteService, times(1)).saveNote(eq(userId), eq(pageUrl), eq(highlightedText), eq(noteContent), isNull(), eq(highlightColor));
    }

    @Test
    @DisplayName("GET /api/notes/page - Empty result when no notes found")
    void testGetNotesByPage_EmptyResult() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = createSession(userId);
        String pageUrl = "/wiki/NonExistent";

        when(noteService.getNotesByUserAndPage(userId, pageUrl))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/notes/page")
                        .session(session)
                        .param("url", pageUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(noteService, times(1)).getNotesByUserAndPage(userId, pageUrl);
    }
}