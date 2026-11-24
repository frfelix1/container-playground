-- The iam schema: who exists, which roles they have, what each role may do.
-- Step 3's Spring Security reads these tables on every login.

CREATE TABLE users (
    id            BIGINT IDENTITY(1, 1) PRIMARY KEY,
    -- UNIQUE: login looks a user up by name, so two "admin" rows would be ambiguous.
    username      NVARCHAR(50)  NOT NULL UNIQUE,
    -- A one-way hash, never the password. 100 fits "{bcrypt}" + a 60-char BCrypt hash with room to spare.
    password_hash NVARCHAR(100) NOT NULL,
    -- BIT = SQL Server's boolean. Lets an admin lock someone out without deleting them.
    enabled       BIT           NOT NULL DEFAULT 1
);

CREATE TABLE roles (
    id   BIGINT IDENTITY(1, 1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE permissions (
    id   BIGINT IDENTITY(1, 1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

-- Join table: one row per (user, role) pair. Many users per role, many roles per user.
-- The composite PRIMARY KEY stops the same pair from being inserted twice.
-- FOREIGN KEY (REFERENCES): the database refuses a pair pointing at a user or role that doesn't exist.
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id),
    role_id BIGINT NOT NULL REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

-- Same idea: one row per (role, permission) pair.
CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles (id),
    permission_id BIGINT NOT NULL REFERENCES permissions (id),
    PRIMARY KEY (role_id, permission_id)
);
