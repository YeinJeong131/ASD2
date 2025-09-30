package betterpedia.notes.controller;

import betterpedia.notes.entity.Note;
import betterpedia.notes.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void testCreateNote_Success() throws Exception {
        // Given
        Long userId = 1L;
        String pageUrl = "/wiki/Java";
        String highlightedText = "Java is...";
        String noteContent = "Important!";

        Note note = new Note(userId, pageUrl, highlightedText, noteContent, null);

        // Controller는 userId를 1L로 하드코딩, position은 null, highlightColor는 파라미터로 받음
        when(noteService.saveNote(eq(1L), eq(pageUrl), eq(highlightedText), eq(noteContent), isNull(), isNull()))
                .thenReturn(note);

        // When & Then
        mockMvc.perform(post("/api/notes")
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
        Note note1 = new Note(userId, "/wiki/Java", "Text1", "Content1", null);
        Note note2 = new Note(userId, "/wiki/Spring", "Text2", "Content2", null);

        List<Note> notes = List.of(note1, note2);  // ✅ 수정
        when(noteService.getAllNotesByUser(userId)).thenReturn(notes);

        // When & Then
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetNotesByPage_Success() throws Exception {
        // Given
        String pageUrl = "/wiki/Java";
        Note note = new Note(1L, pageUrl, "Text", "Content", null);

        when(noteService.getNotesByUserAndPage(anyLong(), eq(pageUrl)))
                .thenReturn(Collections.singletonList(note));  // ✅ 수정

        // When & Then
        mockMvc.perform(get("/api/notes/page")
                        .param("url", pageUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testDeleteNote_Success() throws Exception {
        // Given
        Long noteId = 1L;
        when(noteService.deleteNote(noteId, 1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/notes/{noteId}", noteId))
                .andExpect(status().isOk())
                .andExpect(content().string("Note deleted successfully"));

        verify(noteService, times(1)).deleteNote(noteId, 1L);
    }
}