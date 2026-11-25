package com.provisio.provisioning;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

/*
 * "What does correct mean?" for login + RBAC, written BEFORE any security code.
 *
 * Full app + real SQL Server (Testcontainers), so a login really reads the iam
 * tables seeded by V2. No @MockBean, no fake users: if the SQL that loads a
 * user's permissions is wrong, these tests fail.
 *
 * MockMvc = sends fake HTTP requests straight into Spring MVC, no real port.
 * httpBasic(user, pass) adds the "Authorization: Basic ..." header a client would send.
 *
 * The two status codes this step is about:
 *   401 Unauthorized = "I don't know who you are"  (no or wrong credentials)
 *   403 Forbidden    = "I know who you are, and you may not do this"
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SecurityRulesTest {

    @Autowired
    MockMvc mvc;

    // The Compose healthcheck calls this without credentials, so it must not need a login.
    @Test
    void healthIsOpenWithoutLogin() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void noCredentialsIs401() throws Exception {
        mvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    // The password, not the hash: Spring hashes what you send and compares hashes.
    @Test
    void wrongPasswordIs401() throws Exception {
        mvc.perform(get("/api/me").with(httpBasic("viewer", "wrong-pass")))
                .andExpect(status().isUnauthorized());
    }

    // /api/me = "who am I, and what may I do?" The gui will use it after login (step 5).
    @Test
    void meShowsUserAndPermissionsFromTheDatabase() throws Exception {
        mvc.perform(get("/api/me").with(httpBasic("editor", "editor-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("editor"))
                .andExpect(jsonPath("$.permissions").value(
                        containsInAnyOrder("READ", "WRITE")));
    }

    // SQL Server's default collation compares text case-insensitively, so 'Admin' finds the 'admin' row.
    // Kept on purpose (usernames like emails: any case). Spring takes the name from the DB row,
    // so the app always sees one spelling. On a case-sensitive database this test would fail.
    @Test
    void usernameIsCaseInsensitiveAndNormalisedFromTheDatabase() throws Exception {
        mvc.perform(get("/api/me").with(httpBasic("Admin", "admin-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    /*
     * The RBAC table from MASTER_PLAN.md, as HTTP rules.
     * Rules check PERMISSIONS (READ, WRITE...), never role names: adding a role later
     * is then just rows in the database, no code change.
     *
     * 404 = "got PAST security". There are no project/user controllers until step 4,
     * so an allowed request finds nothing. Step 4 turns these 404s into 200/201/204.
     * 403 = security stopped it before any controller was looked for.
     */
    @ParameterizedTest(name = "{0} {1} {2} -> {3}")
    @CsvSource({
            // user,  method, path,               expected
            "viewer, GET,    /api/projects,       404",
            "viewer, POST,   /api/projects,       403",
            "editor, POST,   /api/projects,       404",
            "editor, PUT,    /api/projects/1,     404",
            "editor, DELETE, /api/projects/1,     403",
            "admin,  DELETE, /api/projects/1,     404",
            "editor, PUT,    /api/users/1/roles,  403",
            "admin,  PUT,    /api/users/1/roles,  404",
    })
    void rbacRules(String user, String method, String path, int expected) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), path)
                        .with(httpBasic(user, user + "-pass")))
                .andExpect(status().is(expected));
    }
}
