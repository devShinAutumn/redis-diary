
package autumn.redisdiary.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "diary_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaryEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer emotionScore;
    private String emotionLabel;

    private Instant createdAt;
    private Instant updatedAt;
}
