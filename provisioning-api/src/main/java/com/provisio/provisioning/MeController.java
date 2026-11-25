package com.provisio.provisioning;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * "Who am I, and what may I do?" The gui calls this right after login (step 5)
 * to decide which buttons to show. The real check still happens in SecurityConfig;
 * hiding a button is convenience, not security.
 */
@RestController
class MeController {

    // A record: an immutable data carrier. Jackson turns it into {"username": ..., "permissions": [...]}.
    record Me(String username, List<String> permissions) {}

    // Spring passes in the Authentication that the security filters already built from the iam tables.
    // Spring Security 7 also adds one FACTOR_* authority per login method used (FACTOR_PASSWORD here),
    // for multi-factor rules. That's "how you logged in", not "what you may do", so we drop it.
    @GetMapping("/api/me")
    Me me(Authentication auth) {
        return new Me(auth.getName(),
                auth.getAuthorities().stream()
                        .filter(a -> !(a instanceof FactorGrantedAuthority))
                        .map(GrantedAuthority::getAuthority)
                        .toList());
    }
}
