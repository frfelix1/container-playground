package com.provisio.execution.project;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/*
 * @WebMvcTest = the "web slice": starts ONLY the web layer (controllers, JSON
 * conversion, error handling). No services, no repositories, no database.
 */
@WebMvcTest
class ProjectControllerTest {

	// A fake HTTP client that calls the controllers in memory, no real network or port.
	@Autowired
	MockMvc mvc;

	// Puts a Mockito fake ProjectService into the Spring context instead of the real one.
	// By default every method returns "empty" (null, Optional.empty(), 0...).
	@MockitoBean
	ProjectService projectService;

	@Test
	void getProjectReturnsProjectAsJson() throws Exception {
		// Arrange: teach the fake what to answer.
		given(projectService.findById(1L)).willReturn(Optional.of(new Project(1L, "Apollo")));

		// Act + Assert: send GET /projects/1 and check status and JSON body.
		mvc.perform(get("/projects/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Apollo"));
	}

	// Kata 1: a project that doesn't exist must give 404, not a crash (500).
	@Test
	void getMissingProjectReturns404() throws Exception {
		// Teach the fake: "project 99? I have nothing" (Optional.empty() = no value).
		given(projectService.findById(99L)).willReturn(Optional.empty());

		mvc.perform(get("/projects/99"))
				.andExpect(status().isNotFound());
	}
}
