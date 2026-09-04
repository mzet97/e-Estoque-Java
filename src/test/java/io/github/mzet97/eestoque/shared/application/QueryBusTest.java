package io.github.mzet97.eestoque.shared.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class QueryBusTest {

    record FindName(String term) implements Query<String> {
    }

    static class FindNameHandler implements QueryHandler<FindName, String> {
        @Override
        public Class<FindName> queryType() {
            return FindName.class;
        }

        @Override
        public String handle(FindName query) {
            return "found:" + query.term();
        }
    }

    @Test
    void dispatchesToRegisteredHandler() {
        var bus = new QueryBus(List.of(new FindNameHandler()));

        var result = bus.dispatch(new FindName("x"));

        assertThat(result).isEqualTo("found:x");
    }
}
