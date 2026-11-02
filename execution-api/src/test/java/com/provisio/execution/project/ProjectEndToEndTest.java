package com.provisio.execution.project;

import static org.assertj.core.api.Assertions.assertThat;

import com.provisio.execution.TestcontainersConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/*
 * End-to-end: real HTTP -> controller -> service -> repository -> SQL Server.
 *
 * RANDOM_PORT = start the WHOLE app on a real embedded Tomcat, on a random free
 * port. Compare @WebMvcTest (ProjectControllerTest): no server at all, MockMvc
 * calls the controller in memory, and the service is a fake.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Real SQL Server in a container, same as the other DB tests.
@Import(TestcontainersConfiguration.class)

//Not transactional since the services wait for each other and hang with it. We have a deleteAll() at the end to clean up after ourselves instead. 
class ProjectEndToEndTest {

    // Spring injects the random port Tomcat picked at startup.
    @LocalServerPort
    int port;

    @Autowired
    ProjectRepository repository;

    @Test
    void getProjectOverRealHttpReturnsSavedProject() {
        // Arrange: put a row in the database.
        Project saved = repository.save(new Project(null, "Apollo"));

        // Act: real HTTP over a real socket. Body read as a raw String,
        // so we see exactly what went over the wire.
        ResponseEntity<String> response = RestClient.create("http://localhost:" + port)
                .get()
                .uri("/projects/{id}", saved.getId())
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("Apollo");
    }

    @AfterEach
    void clean() {
        repository.deleteAll();
    }
}
