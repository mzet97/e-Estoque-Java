package io.github.mzet97.eestoque.shared.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CommandBusTest {

    record Ping(UUID id) implements Command<UUID> {
    }

    static class PingHandler implements CommandHandler<Ping, UUID> {
        @Override
        public Class<Ping> commandType() {
            return Ping.class;
        }

        @Override
        public UUID handle(Ping command) {
            return command.id();
        }
    }

    record Pong() implements Command<Void> {
    }

    @Test
    void dispatchesToRegisteredHandler() {
        var bus = new CommandBus(List.of(new PingHandler()));
        var id = UUID.randomUUID();

        var result = bus.dispatch(new Ping(id));

        assertThat(result).isEqualTo(id);
    }

    @Test
    void failsFastWhenNoHandlerRegistered() {
        var bus = new CommandBus(List.of());

        assertThatThrownBy(() -> bus.dispatch(new Pong()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pong");
    }
}
