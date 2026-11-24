package com.provisio.provisioning;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/*
 * "What does correct mean?" for the iam schema, written BEFORE the migrations.
 * Starts the app against a throwaway SQL Server (Testcontainers), so Flyway
 * runs every V*.sql exactly as it will in Compose. Then we query the result.
 *
 * Why test a schema at all? Step 3's login and RBAC read these tables.
 * If a seed row or a role->permission link is wrong, security is wrong,
 * and this test says so in seconds instead of during a demo.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class IamSchemaTest {

    // JdbcClient: SQL in, rows out. No entities needed just to check data.
    @Autowired
    JdbcClient jdbc;

    // One test run per row: (username, the ONE role it must have).
    @ParameterizedTest
    @CsvSource({"viewer, VIEWER", "editor, EDITOR", "admin, ADMIN"})
    void seedUserHasExactlyItsRole(String username, String role) {
        List<String> roles = jdbc.sql("""
                SELECT r.name FROM users u
                JOIN user_roles ur ON ur.user_id = u.id
                JOIN roles r       ON r.id = ur.role_id
                WHERE u.username = :username
                """)
                .param("username", username)
                .query(String.class)
                .list();

        // containsExactly, not contains: an extra role (say, viewer also ADMIN) must fail.
        assertThat(roles).containsExactly(role);
    }

    // The roles table in MASTER_PLAN.md, as data. Each role lists ALL its permissions
    // (no inheritance in SQL): simpler queries, and the table reads like the spec.
    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "VIEWER | READ",
            "EDITOR | READ,WRITE",
            "ADMIN  | ASSIGN_ROLES,DELETE,READ,WRITE"
    })
    void roleHasExactlyItsPermissions(String role, String expected) {
        List<String> permissions = jdbc.sql("""
                SELECT p.name FROM roles r
                JOIN role_permissions rp ON rp.role_id = r.id
                JOIN permissions p       ON p.id = rp.permission_id
                WHERE r.name = :role
                ORDER BY p.name
                """)
                .param("role", role)
                .query(String.class)
                .list();

        assertThat(permissions).containsExactly(expected.split(","));
    }

    // Never store a password you could read back. "{bcrypt}" is the prefix Spring Security's
    // DelegatingPasswordEncoder (step 3) uses to pick the right algorithm when checking a login.
    @Test
    void passwordsAreStoredAsBcryptHashes() {
        List<String> hashes = jdbc.sql("SELECT password_hash FROM users")
                .query(String.class)
                .list();

        assertThat(hashes).hasSize(3).allMatch(h -> h.startsWith("{bcrypt}$2"));
    }
}
