package io.github.mzet97.eestoque.identity.infrastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Converte o claim <code>realm_access.roles</code> do Keycloak em
 * authorities (equivalente ao KeycloakClaimsTransformation do .NET).
 * As roles viram ROLE_* para uso com hasRole(...) — as escritas exigem
 * ROLE_Create, como o [Authorize(Roles = "Create")] original.
 */
public class KeycloakRealmRolesConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @SuppressWarnings("unchecked")
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (jwt.hasClaim("realm_access")) {
            Object realmAccess = jwt.getClaim("realm_access");
            if (realmAccess instanceof Map<?, ?> access && access.get("roles") instanceof Collection<?> roles) {
                roles.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .forEach(authorities::add);
            }
        }
        return new JwtAuthenticationToken(jwt, List.copyOf(authorities));
    }
}
