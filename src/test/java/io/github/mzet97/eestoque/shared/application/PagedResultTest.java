package io.github.mzet97.eestoque.shared.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PagedResultTest {

    @Test
    void computesPageCountAndRowBoundariesLikeDotNet() {
        var paged = PagedResult.create(2, 10, 35);

        assertThat(paged.currentPage()).isEqualTo(2);
        assertThat(paged.pageCount()).isEqualTo(4);
        assertThat(paged.rowCount()).isEqualTo(35);
        assertThat(paged.firstRowOnPage()).isEqualTo(11);
        assertThat(paged.lastRowOnPage()).isEqualTo(20);
        assertThat(paged.skip()).isEqualTo(10);
    }

    @Test
    void lastRowIsCappedAtRowCount() {
        var paged = PagedResult.create(4, 10, 35);

        assertThat(paged.lastRowOnPage()).isEqualTo(35);
    }

    @Test
    void zeroRowsProduceZeroPages() {
        var paged = PagedResult.create(1, 10, 0);

        assertThat(paged.pageCount()).isZero();
    }
}
