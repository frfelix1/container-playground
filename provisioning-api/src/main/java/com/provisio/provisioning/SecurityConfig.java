package com.provisio.provisioning;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Two questions, answered on every request by Spring Security's filters,
 * BEFORE any controller runs:
 *   1. Who are you?        -> userDetailsService + passwordEncoder below. Fail = 401.
 *   2. May you do this?    -> the authorizeHttpRequests rules.           Fail = 403.
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
                // Credentials arrive on EVERY request in the "Authorization: Basic ..." header.
                .httpBasic(Customizer.withDefaults())
                // ...so no login session is needed. STATELESS = never create one, never send a session cookie.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CSRF abuses cookies the browser attaches by itself. No session cookie here = nothing to abuse.
                // Only the gui's server calls this API; the gui keeps CSRF ON for the browser (step 5).
                .csrf(csrf -> csrf.disable())
                // Rules are checked TOP TO BOTTOM; the first match wins. Specific before general.
                // They name PERMISSIONS, never roles: a new role is just rows in iam, no code change.
                .authorizeHttpRequests(auth -> auth
                        // A real server forwards errors (like 404) to /error. Without this line the forward
                        // itself is checked by the rules below, and a 404 would turn into a 401/403.
                        .requestMatchers("/error").permitAll()
                        // Open on purpose: Compose (and later Kubernetes) asks "are you healthy?" without a login.
                        // Only health: other Actuator endpoints fall through to denyAll below.
                        .requestMatchers(GET, "/actuator/health").permitAll()
                        .requestMatchers(GET, "/api/me").authenticated()
                        .requestMatchers("/api/users/**").hasAuthority("ASSIGN_ROLES")
                        .requestMatchers(GET, "/api/projects/**").hasAuthority("READ")
                        .requestMatchers(POST, "/api/projects/**").hasAuthority("WRITE")
                        .requestMatchers(PUT, "/api/projects/**").hasAuthority("WRITE")
                        .requestMatchers(DELETE, "/api/projects/**").hasAuthority("DELETE")
                        // Anything not listed is refused, even for admin. Forgetting a rule fails closed, not open.
                        .anyRequest().denyAll());
        return http.build();
    }

    /*
     * "Load user X": Spring's ready-made JDBC loader, pointed at OUR tables.
     * Its defaults expect tables named users/authorities; we only swap the two queries.
     */
    @Bean
    UserDetailsService users(DataSource dataSource) {
        JdbcDaoImpl users = new JdbcDaoImpl();
        users.setDataSource(dataSource);
        // Must return exactly 3 columns in this order: username, password, enabled.
        users.setUsersByUsernameQuery(
                "SELECT username, password_hash, enabled FROM users WHERE username = ?");
        // Must return 2 columns: username, authority. One row per permission:
        // user -> user_roles -> role_permissions -> permissions. DISTINCT because two roles may share a permission.
        users.setAuthoritiesByUsernameQuery("""
                SELECT DISTINCT u.username, p.name
                FROM users u
                JOIN user_roles ur       ON ur.user_id = u.id
                JOIN role_permissions rp ON rp.role_id = ur.role_id
                JOIN permissions p       ON p.id = rp.permission_id
                WHERE u.username = ?
                """);
        return users;
    }

    // Reads the "{bcrypt}" prefix on the stored hash to pick the algorithm, hashes the
    // password the client sent with the same salt, and compares the two hashes.
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
