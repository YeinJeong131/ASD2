package betterpedia.notes.controller;

import betterpedia.notes.entity.UserNote;
import betterpedia.notes.repository.UserNoteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class UserNoteController {

    private final UserNoteRepository repo;

    public UserNoteController(UserNoteRepository repo) {
        this.repo = repo;
    }

    // 저장
    @PostMapping
    public UserNote save(@RequestBody UserNote note) {
        return repo.save(note);
    }

    // 조회
    @GetMapping("/user/{userId}")
    public List<UserNote> getByUser(@PathVariable Long userId) {
        return repo.findByUserId(userId);
    }
}
