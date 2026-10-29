package com.provisio.execution.project;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * A Spring Data repository: only an interface, no class, no method bodies.
 * At startup Spring Data generates an object that implements it (a "proxy")
 * and registers it as a bean. Its methods become Hibernate calls, then SQL.
 *
 * <Project, Long> = which entity (so which table: projects) and the type of its @Id field.
 * Free methods include save, findById, findAll, deleteById.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
