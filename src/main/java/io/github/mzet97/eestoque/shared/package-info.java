/**
 * Shared kernel: entidades base, eventos, exceções, envelope de resposta,
 * paginação e barramentos de Command/Query. Aberto porque todo bounded
 * context depende dele.
 */
@org.springframework.modulith.ApplicationModule(type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package io.github.mzet97.eestoque.shared;
