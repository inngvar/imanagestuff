package org.manage.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A PollingState.
 */
@Entity
@Table(name = "polling_state")
@RegisterForReflection
public class PollingState extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id; // Always 1

    @Column(name = "last_update_id")
    public Long lastUpdateId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PollingState)) {
            return false;
        }
        return id != null && id.equals(((PollingState) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PollingState{" +
            "id=" + id +
            ", lastUpdateId='" + lastUpdateId + "'" +
            "}";
    }
}
