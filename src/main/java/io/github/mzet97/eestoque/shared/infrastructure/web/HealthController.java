package io.github.mzet97.eestoque.shared.infrastructure.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API-INFRA-001: compatibilidade com o /health usado pelo load balancer e
 * pelos healthchecks do compose original.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of("status", "Healthy");
    }
}
