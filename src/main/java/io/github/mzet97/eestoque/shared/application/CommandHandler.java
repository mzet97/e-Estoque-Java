package io.github.mzet97.eestoque.shared.application;

/**
 * Handler de um Command. Bean injetado diretamente onde é usado — a camada
 * web conhece o handler concreto (sem bus de mediação).
 */
public interface CommandHandler<C extends Command<R>, R> {

    R handle(C command);
}
