package com.provisio.execution.project;

/*
 * A record = an immutable data class. Java generates the constructor,
 * accessors (id(), name()), equals/hashCode and toString for you.
 * Jackson turns it into JSON as {"id":1,"name":"..."}.
 *
 * For now this is just the shape we send over HTTP. It becomes a database
 * entity later, when the repository layer arrives.
 */
public record Project(Long id, String name) {
}
