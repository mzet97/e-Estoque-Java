package io.github.mzet97.eestoque.shared.application;

/**
 * Handler de um Command. O tipo do command é declarado explicitamente
 * (proxy-safe, sem reflexão sobre generics).
 */
public interface CommandHandler<C extends Command<R>, R> {

    Class<C> commandType();

    R handle(C command);
}
