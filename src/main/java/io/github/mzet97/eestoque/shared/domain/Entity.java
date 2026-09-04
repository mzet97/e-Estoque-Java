package io.github.mzet97.eestoque.shared.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base de todas as entidades do domínio. Preserva o contrato do sistema
 * original: identidade UUID, auditoria (createdAt/updatedAt/deletedAt),
 * soft delete (deleted/deletedAt) e eventos de domínio acumulados.
 *
 * Entidades acumulam erros de validação em {@link #validate()} sem lançar
 * exceção — o handler de aplicação decide o que fazer (paridade com o
 * comportamento do sistema .NET).
 */
public abstract class Entity {

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private boolean deleted;

    private final transient List<DomainEvent> events = new ArrayList<>();
    private transient List<String> errors = List.of();
    private transient boolean valid;

    protected Entity(UUID id, Instant createdAt) {
        this(id, createdAt, null, null, false);
    }

    protected Entity(UUID id, Instant createdAt, Instant updatedAt, Instant deletedAt, boolean deleted) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.deleted = deleted;
    }

    public UUID id() {
        return id;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant deletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    /** Recarcula os erros de validação. Implementado por cada entidade. */
    public abstract void validate();

    protected final void markValid() {
        this.errors = List.of();
        this.valid = true;
    }

    protected final void markInvalid(List<String> errors) {
        this.errors = List.copyOf(errors);
        this.valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> errors() {
        return errors;
    }

    /** Mensagens de validação unidas por vírgula, como no contrato .NET. */
    public String joinedErrors() {
        return String.join(",", errors);
    }

    protected final void registerEvent(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> domainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }

    protected final void touch(Instant now) {
        this.updatedAt = now;
    }

    /** Soft delete: mantém a linha com isDeleted=true e deletedAt. */
    public void disable(Instant now) {
        this.deleted = true;
        this.deletedAt = now;
    }

    /** Reversão do soft delete. */
    public void activate() {
        this.deleted = false;
        this.deletedAt = null;
    }
}
