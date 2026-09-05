package io.github.mzet97.eestoque.shared.application;

/** Handler de uma Query. Bean injetado diretamente onde é usado. */
public interface QueryHandler<Q extends Query<R>, R> {

    R handle(Q query);
}
