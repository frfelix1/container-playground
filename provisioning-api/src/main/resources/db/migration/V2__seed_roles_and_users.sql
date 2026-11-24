-- Seed data in its own migration: V1 is the structure, V2 the starting content.
-- Lookups by name, not hardcoded ids: IDENTITY values are the database's business.

INSERT INTO roles (name) VALUES ('VIEWER'), ('EDITOR'), ('ADMIN');
INSERT INTO permissions (name) VALUES ('READ'), ('WRITE'), ('DELETE'), ('ASSIGN_ROLES');

-- MASTER_PLAN.md's roles table, spelled out in full (no inheritance in SQL).
-- CROSS JOIN + WHERE = "every (role, permission) combination that matches this list".
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p
WHERE (r.name = 'VIEWER' AND p.name IN ('READ'))
   OR (r.name = 'EDITOR' AND p.name IN ('READ', 'WRITE'))
   OR (r.name = 'ADMIN'  AND p.name IN ('READ', 'WRITE', 'DELETE', 'ASSIGN_ROLES'));

-- shortcut: demo users with known passwords (viewer-pass, editor-pass, admin-pass) live in a migration, so every environment gets them; move to a dev-only seed before anything faces real users.
-- Hashes made with: htpasswd -bnBC 10 "" viewer-pass  ($2y changed to $2a, same algorithm).
INSERT INTO users (username, password_hash) VALUES
    ('viewer', '{bcrypt}$2a$10$jPPWYjd.xeTQUTgzaQTowuG0m2qOXA7iVdw8HnNmo.Lf/dtVhK29a'),
    ('editor', '{bcrypt}$2a$10$ifX5.1Dtx5IUPKAR/c8e5uPDA7Uaiem7MIkmGNjK0jyQqqfEevnUa'),
    ('admin',  '{bcrypt}$2a$10$UFPbSfoKmTkrnnwMTnw6Eu11zOcK9PI4s8241pZpylCUQLq3D9YZS');

-- Each user gets exactly the role with the same name, uppercased.
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = UPPER(u.username);
