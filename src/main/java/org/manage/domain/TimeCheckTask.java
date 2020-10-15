package org.manage.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A TimeCheckTask.
 */
@Entity
@Table(name = "time_check_task")
@RegisterForReflection
public class TimeCheckTask extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    public LocalDate date;

    @OneToMany(mappedBy = "timeCheckTask")
    public Set<TimeLog> checks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeCheckTask)) {
            return false;
        }
        return id != null && id.equals(((TimeCheckTask) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TimeCheckTask{" +
            "id=" + id +
            ", date='" + date + "'" +
            "}";
    }

    public TimeCheckTask update() {
        return update(this);
    }

    public TimeCheckTask persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static TimeCheckTask update(TimeCheckTask timeCheckTask) {
        if (timeCheckTask == null) {
            throw new IllegalArgumentException("timeCheckTask can't be null");
        }
        var entity = TimeCheckTask.<TimeCheckTask>findById(timeCheckTask.id);
        if (entity != null) {
            entity.date = timeCheckTask.date;
            entity.checks = timeCheckTask.checks;
        }
        return entity;
    }

    public static TimeCheckTask persistOrUpdate(TimeCheckTask timeCheckTask) {
        if (timeCheckTask == null) {
            throw new IllegalArgumentException("timeCheckTask can't be null");
        }
        if (timeCheckTask.id == null) {
            persist(timeCheckTask);
            return timeCheckTask;
        } else {
            return update(timeCheckTask);
        }
    }


}
