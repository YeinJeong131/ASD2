package betterpedia.notes.controller;

import betterpedia.notes.entity.Note;
import betterpedia.notes.service.NoteService;
import betterpedia.user.entity.User;
import org.junit.jupiter.api.Test;
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
    void testCreateNote_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        String pageUrl = "/wiki/Java";
        String highlightedText = "Java is...";
        String noteContent = "Important!";

        Note note = new Note(user, pageUrl, highlightedText, noteContent, null);

        when(noteService.saveNote(eq(1L), eq(pageUrl), eq(highlightedText), eq(noteContent), isNull(), isNull()))
                .thenReturn(note);

        // When & Then
        mockMvc.perform(post("/api/notes")
                        .session(session)
                        .param("pageUrl", pageUrl)
                        .param("highlightedText", highlightedText)
                        .param("noteContent", noteContent))
                .andExpect(status().isOk());

        verify(noteService, times(1)).saveNote(eq(1L), eq(pageUrl), eq(highlightedText), eq(noteContent), isNull(), isNull());
    }

    @Test
    void testGetAllNotes_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        Note note1 = new Note(user, "/wiki/Java", "Text1", "Content1", null);
        Note note2 = new Note(user, "/wiki/Spring", "Text2", "Content2", null);

        List<Note> notes = Arrays.asList(note1, note2);
        when(noteService.getAllNotesByUser(userId)).thenReturn(notes);

        // When & Then
        mockMvc.perform(get("/api/notes")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetNotesByPage_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        String pageUrl = "/wiki/Java";
        Note note = new Note(user, pageUrl, "Text", "Content", null);

        when(noteService.getNotesByUserAndPage(anyLong(), eq(pageUrl)))
                .thenReturn(Collections.singletonList(note));

        // When & Then
        mockMvc.perform(get("/api/notes/page")
                        .session(session)
                        .param("url", pageUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateNote_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser(userId);
        MockHttpSession session = createSession(userId);

        Long noteId = 1L;
        String newContent = "Updated content";
        Note updatedNote = new Note(user, "/wiki/Java", "Text", newContent, null);
        updatedNote.setNoteId(noteId);

        when(noteService.updateNote(eq(1L), eq(noteId), eq(newContent), isNull()))
                .thenReturn(updatedNote);

        // When & Then
        mockMvc.perform(put("/api/notes/{noteId}", noteId)
                        .session(session)
                        .param("noteContent", newContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteContent").value(newContent));

        verify(noteService, times(1)).updateNote(eq(1L), eq(noteId), eq(newContent), isNull());
    }

    @Test
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
    }

    @Test
    void testCreateNote_NotLoggedIn() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/notes")
                        .param("pageUrl", "/wiki/Java")
                        .param("highlightedText", "text")
                        .param("noteContent", "content"))
                .andExpect(status().isUnauthorized());
    }
}