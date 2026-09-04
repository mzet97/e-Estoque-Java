package io.github.mzet97.eestoque.shared.application;

/** Handler de uma Query. */
public interface QueryHandler<Q extends Query<R>, R> {

    Class<Q> queryType();

    R handle(Q query);
}
