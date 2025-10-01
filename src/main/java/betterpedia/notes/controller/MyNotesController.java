package betterpedia.notes.controller;

import betterpedia.notes.entity.Note;
import betterpedia.notes.service.NoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/my-notes")
public class MyNotesController {
    @Autowired
    private NoteService noteService;


    @GetMapping("")
    public String showMyNotes(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");


        if (userId == null) {
            return "redirect:/login";
        }

        try {
            List<Note> allNotes = noteService.getAllNotesByUser(userId);

            Map<String, List<Note>> notesByPage = allNotes.stream()
                    .collect(Collectors.groupingBy(Note::getPageUrl));

            model.addAttribute("allNotes", allNotes);
            model.addAttribute("notesByPage", notesByPage);
            model.addAttribute("totalNotes", allNotes.size());
            model.addAttribute("userId", userId);

            return "notes/my-notes";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load notes: " + e.getMessage());
            return "notes/my-notes";
        }
    }
}