package com.provisio.execution.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import com.provisio.execution.TestcontainersConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

// Full app context with real beans (real service, real repository).
@SpringBootTest
// Real SQL Server in a container, same as contextLoads.
@Import(TestcontainersConfiguration.class)
// Each test runs in a transaction that is rolled back afterwards: rows a test inserts disappear.
@Transactional
class ProjectServiceTest {

    // Spring fills these fields with the beans. Fine in tests; production code uses constructor injection.
    @Autowired
    ProjectService service;

    @Autowired
    ProjectRepository repository;

    @Test
    void findByIdReturnsSavedProject() {
        // null id = the database picks it (IDENTITY).
        Project saved = repository.save(new Project(null, "Apollo"));

        Optional<Project> found = service.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Apollo");
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertThat(service.findById(999_999L)).isEmpty();
    }

    /*
     * Sprint 1, kata 2a: list all projects.
     * containsExactly (not just "contains") also proves the rollback: if rows
     * from the other tests in this class survived, the list would have extras.
     */
    @Test
    void findAllReturnsEverySavedProject() {
        repository.save(new Project(null, "Apollo"));
        repository.save(new Project(null, "Gemini"));

        List<Project> all = service.findAll();

        assertThat(all).extracting(Project::getName).containsExactlyInAnyOrder("Apollo", "Gemini");
    }

    @Test
    void saveRejectsNameLongerThan300Chars() {
        
        String longName = "a".repeat(301);

        assertThatThrownBy(() -> repository.save(new Project(null, longName))).isInstanceOf(DataAccessException.class).hasMessageContaining("truncated");
    }
}
