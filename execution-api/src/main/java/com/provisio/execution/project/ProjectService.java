package com.provisio.execution.project;

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
}
