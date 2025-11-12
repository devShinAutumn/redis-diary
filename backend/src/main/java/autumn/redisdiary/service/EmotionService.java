
package autumn.redisdiary.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * EmotionService - OpenAI Chat Completions 기반 감정분석 서비스
 *
 * 동작 방식:
 * - OpenAI Chat Completions (POST /v1/chat/completions) 호출
 * - 모델에게 "JSON 형식: {"label":"...","score":숫자}" 만 반환하도록 프롬프트 지시
 * - 응답 텍스트를 파싱하여 label/score를 반환
 *
 * 환경변수:
 * - OPENAI_API_KEY : OpenAI API 키
 * - openai.api-base : (선택) API base URL, 기본 https://api.openai.com/v1
 * - openai.model : 사용할 모델 id (예: gpt-4o-mini, gpt-4o, gpt-3.5-turbo 등)
 *
 * 주의:
 * - 실제 운영 시 rate limit, 비용, 오류 핸들링을 더 정교하게 처리하세요.
 */
@Service
public class EmotionService {

    private final WebClient webClient;
    private final String model;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 타임아웃/리트라이/캐시 등은 필요하면 추가하세요.
    public EmotionService(
            @Value("${openai.api-base:https://api.openai.com/v1}") String apiBase,
            @Value("${openai.model:gpt-3.5-turbo}") String model,
            @Value("${OPENAI_API_KEY:}") String apiKey
    ) {
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(apiBase)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public EmotionResult analyze(String text) {
        // 안전장치: 빈 텍스트일 경우 기본 반환
        if (text == null || text.isBlank()) {
            return new EmotionResult(0, "neutral");
        }

        // 프롬프트: 모델이 정확히 JSON 하나만 반환하도록 지시
        String systemPrompt = "You are an assistant that analyzes the sentiment/emotion of a piece of text. "
                + "Return EXACTLY one JSON object (and nothing else) in the format: "
                + "{\"label\":\"<one of: happy, sad, angry, neutral, surprised, anxious, calm>\", \"score\":<integer between -100 and 100>}.";

        String userPrompt = "Analyze the following diary text and return the JSON described above. Text:\n\n" + text;

        // Build request body for Chat Completions
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "max_tokens", 100,
                "temperature", 0.0
        );

        try {
            Mono<String> mono = webClient.post()
                    .uri("/chat/completions")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(15));

            String raw = mono.block(); // template: blocking call for simplicity
            if (raw == null) {
                return fallbackAnalyze(text);
            }

            // Try to parse JSON from response.
            // OpenAI chat completions response shape: { choices: [ { message: { content: "..." } } ], ... }
            JsonNode root = objectMapper.readTree(raw);
            JsonNode choices = root.path("choices"); 
            if (choices.isArray() && choices.size() > 0) {
                String content = choices.get(0).path("message").path("content").asText(""); 
                // content should be a JSON string like {"label":"happy","score":80}
                // Try parsing content as JSON node; if there's extra text, try to extract the first {...}
                JsonNode parsed = tryParseJsonFromString(content);
                if (parsed != null) {
                    String label = parsed.path("label").asText("neutral");
                    int score = parsed.path("score").asInt(0);
                    return new EmotionResult(score, label);
                } else {
                    // last-chance: try to parse root for label/score keys
                    JsonNode top = tryParseJsonFromString(raw);
                    if (top != null && (top.has("label") || top.has("score"))) {
                        String label = top.path("label").asText("neutral");
                        int score = top.path("score").asInt(0);
                        return new EmotionResult(score, label);
                    }
                }
            }
            // Fallback to naive local analysis
            return fallbackAnalyze(text);
        } catch (Exception e) {
            // On any error, fallback to a simple deterministic heuristic
            return fallbackAnalyze(text);
        }
    }

    private EmotionResult fallbackAnalyze(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("happy") || lower.contains("joy") || lower.contains("좋")) return new EmotionResult(80, "happy");
        if (lower.contains("sad") || lower.contains("우울") || lower.contains("슬")) return new EmotionResult(-70, "sad");
        if (lower.contains("angry") || lower.contains("화")) return new EmotionResult(-80, "angry");
        return new EmotionResult(0, "neutral");
    }

    /**
     * Try to parse a JSON object from a string. If the string contains some text + JSON,
     * this tries to find the first '{' ... '}' and parse it.
     */
    private JsonNode tryParseJsonFromString(String s) {
        if (s == null) return null;
        s = s.trim();
        try {
            // direct parse
            return objectMapper.readTree(s);
        } catch (Exception ignored) {
        }
        // attempt to find first {...}
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        if (start >= 0 && end > start) {
            String sub = s.substring(start, end + 1);
            try {
                return objectMapper.readTree(sub);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public record EmotionResult(int score, String label) {}
}
