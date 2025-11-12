
package autumn.redisdiary.controller;

import autumn.redisdiary.entity.DiaryEntry;
import autumn.redisdiary.entity.User;
import autumn.redisdiary.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDiaryRequest req) {
        // NOTE: In this template we do not implement full security context retrieval.
        // Replace getTestUser() with actual authenticated user lookup (SecurityContext).
        User user = getTestUser();
        DiaryEntry d = diaryService.createDiary(user, req.title(), req.content());
        return ResponseEntity.ok(d);
    }

    record CreateDiaryRequest(String title, String content) {}
    private User getTestUser() {
        return User.builder().id(1L).username("test").email("t@t.com").build();
    }
}
