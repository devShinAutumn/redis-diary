
package autumn.redisdiary.repository;

import autumn.redisdiary.entity.DiaryEntry;
import autumn.redisdiary.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {
    Page<DiaryEntry> findByUser(User user, Pageable pageable);
    List<DiaryEntry> findByUserAndCreatedAtBetween(User user, Instant start, Instant end);
}
