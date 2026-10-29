package com.provisio.execution.project;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * An entity = a Java object that represents ONE row in a table, and that
 * Hibernate (the translator) manages over time.
 *
 * Why a normal class and not a record? Hibernate needs three things a record
 * can't give it:
 *  1. A no-argument constructor: it creates an empty object, then fills the fields.
 *  2. Fields that can change: it writes the fields itself and tracks your changes.
 *  3. A class that can be subclassed: it sometimes hands you a stand-in subclass
 *     that loads data later (records are always final).
 *
 * @Entity = "Hibernate, manage this class and map it to a table."
 * @Table(name = "projects") = the table's real name. Without it, Hibernate would
 * derive the name from the class and look for "project" (singular), which doesn't exist.
 */
@Entity
@Table(name = "projects")
public class Project {

	/*
	 * @Id = this field is the primary key.
	 * @GeneratedValue(IDENTITY) = "the database hands out the id", matching
	 * IDENTITY(1, 1) in V1__create_projects.sql. On insert, Hibernate leaves id
	 * out and reads back the number SQL Server picked.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// No annotation needed: maps to the column with the same name, "name".
	private String name;

	// For Hibernate only. protected = our own code can't easily create a half-empty Project.
	protected Project() {
	}

	// For our own code and tests: a complete Project in one step.
	public Project(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	/*
	 * Getters. The record generated id() and name() for us; a class doesn't.
	 * Jackson finds getXxx() methods to build the JSON {"id":1,"name":"..."}.
	 * Without them the controller would return an empty object.
	 */
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
