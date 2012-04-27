package no.kantega.kwashc.server.repository;

import no.kantega.kwashc.server.model.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author andbat
 */
@Transactional
public interface TestRunRepository extends JpaRepository<TestRun, Long> {
}
