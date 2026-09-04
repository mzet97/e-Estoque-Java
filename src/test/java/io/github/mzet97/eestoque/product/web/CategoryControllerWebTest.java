package io.github.mzet97.eestoque.product.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import io.github.mzet97.eestoque.identity.infrastructure.SecurityConfiguration;
import io.github.mzet97.eestoque.product.application.CategoryViewModel;
import io.github.mzet97.eestoque.product.application.CreateCategoryCommand;
import io.github.mzet97.eestoque.product.application.GetCategoryByIdQuery;
import io.github.mzet97.eestoque.product.application.SearchCategoriesQuery;
import io.github.mzet97.eestoque.product.application.UpdateCategoryCommand;
import io.github.mzet97.eestoque.shared.application.BaseResult;
import io.github.mzet97.eestoque.shared.application.BaseResultList;
import io.github.mzet97.eestoque.shared.application.CommandBus;
import io.github.mzet97.eestoque.shared.application.QueryBus;
import io.github.mzet97.eestoque.shared.domain.NotFoundException;

/**
 * Contrato HTTP + regras de autorização (FR-CAT, NFR-SEC-002).
 */
@WebMvcTest(CategoryController.class)
@Import(SecurityConfiguration.class)
class CategoryControllerWebTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CommandBus commands;

    @Autowired
    QueryBus queries;

    @TestConfiguration
    static class BusStubs {
        @Bean
        CommandBus commandBus() {
            return org.mockito.Mockito.mock(CommandBus.class);
        }

        @Bean
        QueryBus queryBus() {
            return org.mockito.Mockito.mock(QueryBus.class);
        }
    }

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    JwtDecoder jwtDecoder;

    private static RequestPostProcessor writeRole() {
        return jwt().jwt(builder -> builder.claim("realm_access", java.util.Map.of("roles",
                        java.util.List.of("Create"))))
                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_Create"));
    }

    private static RequestPostProcessor readRole() {
        return jwt().jwt(builder -> builder.claim("realm_access", java.util.Map.of("roles",
                        java.util.List.of("user"))))
                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_user"));
    }

    @Test
    void searchWithoutTokenIs401() throws Exception {
        mockMvc.perform(get("/api/Categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchWithValidTokenReturnsEnvelope() throws Exception {
        when(queries.dispatch(any(SearchCategoriesQuery.class))).thenReturn(
                BaseResultList.of(java.util.List.of(
                        new CategoryViewModel(UUID.randomUUID(), "Bebidas", "Desc", "Beb", null, null, null)),
                        new io.github.mzet97.eestoque.shared.application.PagedResult(1, 1, 10, 1)));

        mockMvc.perform(get("/api/Categories").param("name", "Bebidas").with(readRole()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.pagedResult.currentPage").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Bebidas"));
    }

    @Test
    void createWithCreateRoleReturns201AndLocation() throws Exception {
        var id = UUID.randomUUID();
        when(commands.dispatch(any(CreateCategoryCommand.class))).thenReturn(id);
        when(queries.dispatch(any(GetCategoryByIdQuery.class))).thenReturn(
                BaseResult.of(new CategoryViewModel(id, "Bebidas", "Desc", "Beb", null, null, null)));

        mockMvc.perform(post("/api/Categories")
                        .with(writeRole())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bebidas\",\"description\":\"Desc\",\"shortDescription\":\"Beb\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(id.toString()));
    }

    @Test
    void createWithoutCreateRoleIs403() throws Exception {
        mockMvc.perform(post("/api/Categories")
                        .with(readRole())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bebidas\",\"description\":\"Desc\",\"shortDescription\":\"Beb\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void putOverridesRouteIdLikeMigrationDifferenceMD01() throws Exception {
        var routeId = UUID.randomUUID();
        var bodyId = UUID.randomUUID();
        when(commands.dispatch(any(UpdateCategoryCommand.class))).thenReturn(routeId);
        when(queries.dispatch(any(GetCategoryByIdQuery.class))).thenReturn(
                BaseResult.of(new CategoryViewModel(routeId, "Bebidas", "Desc", "Beb", null, null, null)));

        mockMvc.perform(put("/api/Categories/" + routeId)
                        .with(writeRole())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + bodyId + "\",\"name\":\"Bebidas\",\"description\":\"Desc\","
                                + "\"shortDescription\":\"Beb\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(routeId.toString()));
    }

    @Test
    void deleteReturnsEmptyObjectAnd200() throws Exception {
        mockMvc.perform(delete("/api/Categories/" + UUID.randomUUID()).with(writeRole()))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void getMissingCategoryReturns404WithErrorEnvelope() throws Exception {
        when(queries.dispatch(any(GetCategoryByIdQuery.class))).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/Categories/" + UUID.randomUUID()).with(writeRole()))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"Not found\"}"));
    }
}
