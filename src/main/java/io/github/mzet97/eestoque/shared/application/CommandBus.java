package io.github.mzet97.eestoque.shared.application;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Despacha Commands para o handler registrado. Equivalente mínimo e
 * idiomático do MediatR do sistema original (ADR-004).
 */
@Component
public class CommandBus {

    private final Map<Class<?>, CommandHandler<?, ?>> handlers;

    public CommandBus(List<CommandHandler<?, ?>> discovered) {
        this.handlers = discovered.stream()
                .collect(Collectors.toUnmodifiableMap(CommandHandler::commandType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <R> R dispatch(Command<R> command) {
        var handler = (CommandHandler<Command<R>, R>) handlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalStateException("No CommandHandler registered for " + command.getClass().getName());
        }
        return handler.handle(command);
    }
}
