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

	public Optional<Project> findById(Long id) {
		// Not implemented yet: the repository layer comes later.
		// The controller test replaces this whole class with a mock, so it never runs there.
		throw new UnsupportedOperationException("not implemented yet");
	}
}
