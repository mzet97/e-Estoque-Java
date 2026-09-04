package io.github.mzet97.eestoque.shared.infrastructure.web;

import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Convenção OData/.NET de item por key: /odata/Categories({id}) — um único
 * segmento com parênteses. PathPattern (Spring 6+) não permite variável no
 * meio do segmento, então reescrevemos para /odata/Categories/{id} antes do
 * roteamento.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ODataKeyPathRewriteFilter extends OncePerRequestFilter {

    private static final Pattern KEY_PATH = Pattern.compile("^/odata/([A-Za-z]+)\\(([^()/]+)\\)$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var uri = request.getRequestURI();
        var matcher = KEY_PATH.matcher(uri);
        if (matcher.matches()) {
            var rewritten = "/odata/" + matcher.group(1) + "/" + matcher.group(2);
            var wrapped = new HttpServletRequestWrapper(request) {
                @Override
                public String getRequestURI() {
                    return rewritten;
                }

                @Override
                public String getServletPath() {
                    return rewritten;
                }
            };
            chain.doFilter(wrapped, response);
            return;
        }
        chain.doFilter(request, response);
    }
}
