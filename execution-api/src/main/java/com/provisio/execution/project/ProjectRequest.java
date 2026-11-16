package com.provisio.execution.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * The JSON body a client sends for POST and PUT: {"name": "Apollo"}.
 *
 * Why not accept a Project directly? Project is the entity: it has an id the
 * database owns. If the controller took a Project, a client could send
 * {"id": 5, ...} and try to choose or overwrite ids. A separate request type
 * (a "DTO", data transfer object) lists only what a client may send.
 *
 * A record is fine here (unlike Project): Jackson can build records, and
 * nothing needs to change the values after they arrive.
 *
 * The annotations are only rules. They do nothing until someone checks them:
 * @Valid on the controller parameter makes Spring check them before the method
 * runs, and answer 400 if any fail.
 *
 * @NotBlank = not null, not "", not "   ".
 * @Size(max = 300) = same limit as V3__change_name_length.sql (NVARCHAR(300)).
 * Java rejects it first with a clean 400; the column is the last line of defence.
 */
public record ProjectRequest(@NotBlank @Size(max = 300) String name) {
}
