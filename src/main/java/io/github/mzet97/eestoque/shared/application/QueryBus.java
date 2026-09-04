package io.github.mzet97.eestoque.shared.application;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/** Despacha Queries para o handler registrado. */
@Component
public class QueryBus {

    private final Map<Class<?>, QueryHandler<?, ?>> handlers;

    public QueryBus(List<QueryHandler<?, ?>> discovered) {
        this.handlers = discovered.stream()
                .collect(Collectors.toUnmodifiableMap(QueryHandler::queryType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <R> R dispatch(Query<R> query) {
        var handler = (QueryHandler<Query<R>, R>) handlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalStateException("No QueryHandler registered for " + query.getClass().getName());
        }
        return handler.handle(query);
    }
}
