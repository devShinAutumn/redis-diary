
package autumn.redisdiary.service;

import autumn.redisdiary.repository.DiaryRepository;
import autumn.redisdiary.entity.Diary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private EmotionService emotionService;

    // 특정 일기 조회 + 캐싱
    public Diary getDiary(Long id) {
        String cacheKey = "diary:" + id;
        Diary cached = (Diary) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        Diary diary = diaryRepository.findById(id).orElseThrow();
        redisTemplate.opsForValue().set(cacheKey, diary, Duration.ofMinutes(15));
        return diary;
    }

    // 최근 일기 목록 조회 + 캐싱
    public List<Diary> getRecentDiaries(Long userId) {
        String cacheKey = "diary:recent:" + userId;
        List<Diary> cached = (List<Diary>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        List<Diary> list = diaryRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        redisTemplate.opsForValue().set(cacheKey, list, Duration.ofMinutes(5));
        return list;
    }

    // 감정 분석 + 캐싱
    public EmotionService.EmotionResult analyzeEmotion(Diary diary) {
        String cacheKey = "emotion:diary:" + diary.getId();
        EmotionService.EmotionResult cached = (EmotionService.EmotionResult) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        EmotionService.EmotionResult result = emotionService.analyze(diary.getContent());
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofHours(1));
        return result;
    }
}
