package io.github.mzet97.eestoque.outbox;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxSpringDataRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop50ByStatusOrderByOccurredAtAsc(String status);
}
