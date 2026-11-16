package com.provisio.execution.project;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // No path = the class path itself: GET /projects. Always 200; "none" is just [].
    @GetMapping
    public List<Project> findAll() {
        return this.projectService.findAll();
    }

    /*
     * POST /projects with {"name": "..."}.
     * @RequestBody = build the parameter from the JSON body (Jackson).
     * @Valid = check ProjectRequest's annotations first; on failure Spring
     * answers 400 and this method never runs.
     * 201 Created + Location header = REST convention: "made it, it lives here".
     */
    @PostMapping
    public ResponseEntity<Project> create(@Valid @RequestBody ProjectRequest request) {
        Project created = projectService.create(request.name());
        return ResponseEntity.created(URI.create("/projects/" + created.getId())).body(created);
    }

    // PUT /projects/{id}: same 200-or-404 shape as getById.
    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request.name())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 204 No Content = deleted, nothing to send back. ResponseEntity<Void> = no body.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return projectService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
