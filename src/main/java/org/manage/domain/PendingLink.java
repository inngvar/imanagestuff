package org.manage.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A PendingLink.
 */
@Entity
@Table(name = "pending_link")
@RegisterForReflection
public class PendingLink extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    public Member member;

    @Column(name = "code", unique = true)
    public String code;

    @Column(name = "created_at")
    public Instant createdAt = Instant.now();

    @Column(name = "expires_at")
    public Instant expiresAt;

    @Column(name = "used")
    public boolean used = false;

    public PendingLink update() {
        return update(this);
    }

    public PendingLink persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static PendingLink update(PendingLink pendingLink) {
        if (pendingLink == null) {
            throw new IllegalArgumentException("pendingLink can't be null");
        }
        var entity = PendingLink.<PendingLink>findById(pendingLink.id);
        if (entity != null) {
            entity.member = pendingLink.member;
            entity.code = pendingLink.code;
            entity.createdAt = pendingLink.createdAt;
            entity.expiresAt = pendingLink.expiresAt;
            entity.used = pendingLink.used;
        }
        return entity;
    }

    public static PendingLink persistOrUpdate(PendingLink pendingLink) {
        if (pendingLink == null) {
            throw new IllegalArgumentException("pendingLink can't be null");
        }
        if (pendingLink.id == null) {
            persist(pendingLink);
            return pendingLink;
        } else {
            return update(pendingLink);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PendingLink)) {
            return false;
        }
        return id != null && id.equals(((PendingLink) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PendingLink{" +
            "id=" + id +
            ", code='" + code + "'" +
            ", createdAt='" + createdAt + "'" +
            ", expiresAt='" + expiresAt + "'" +
            ", used='" + used + "'" +
            "}";
    }
}
