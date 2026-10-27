package com.provisio.execution.project;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @RestController = "this class handles HTTP requests, and whatever my methods
 * return is written to the response body as JSON" (Jackson does the conversion).
 * It's also a bean, so Spring creates it on startup and @WebMvcTest picks it up.
 *
 * @RequestMapping("/projects") = every URL in this class starts with /projects.
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

	private final ProjectService projectService;

	/*
	 * Constructor injection: "I can't work without a ProjectService."
	 * Spring sees this constructor and passes in the ProjectService bean
	 * (in the test: the Mockito fake). You never call `new ProjectController(...)`.
	 */
	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	/*
	 * @GetMapping("/{id}") = handle GET /projects/{anything}.
	 * @PathVariable Long id = take the {id} part of the URL ("1", which is text)
	 * and convert it into a Java number (1L) for us.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Project> getById(@PathVariable Long id) {
		// orElseThrow(): unwrap the Optional, or throw an exception if it's empty.
		// Deliberately minimal: "not found" is not handled properly yet.
        return projectService.findById(id)
		.map(ResponseEntity::ok)
		.orElseGet(() -> ResponseEntity.notFound().build());
    }
}
