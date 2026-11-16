package com.provisio.execution.project;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/*
 * The logic layer. @Service registers this class as a Spring bean, so Spring
 * can hand it to any class that asks for it (dependency injection).
 *
 * Optional<Project> = "maybe a project". Empty means "not found". The service
 * says "not found" without knowing anything about HTTP or 404.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {

        this.projectRepository = projectRepository;
    }
    public Optional<Project> findById(Long id) {

        return this.projectRepository.findById(id);
    }

    public List<Project> findAll() {
        return this.projectRepository.findAll();
    }

    // id null = "new row": save() does an INSERT and SQL Server picks the id (IDENTITY).
    public Project create(String name) {
        return this.projectRepository.save(new Project(null, name));
    }

    /*
     * Find, change, save. save() on an entity that has an id does an UPDATE.
     * Empty Optional = "no such project"; the controller turns that into 404.
     */
    public Optional<Project> update(Long id, String name) {
        return this.projectRepository.findById(id)
                .map(project -> {
                    project.setName(name);
                    return this.projectRepository.save(project);
                });
    }

    /*
     * true = deleted, false = it didn't exist. Checking first is two queries
     * instead of one; fine here, and it keeps "not found" out of exception handling.
     */
    public boolean delete(Long id) {
        if (!this.projectRepository.existsById(id)) {
            return false;
        }
        this.projectRepository.deleteById(id);
        return true;
    }
}
