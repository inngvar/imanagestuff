package org.manage.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A TimeLog.
 */
@Entity
@Table(name = "time_log")
@RegisterForReflection
public class TimeLog extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    public ZonedDateTime timestamp;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    @NotNull
    @JsonbTransient
    public Member member;

    @ManyToOne
    @JoinColumn(name = "time_check_task_id")
    @JsonbTransient
    public TimeCheckTask timeCheckTask;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeLog)) {
            return false;
        }
        return id != null && id.equals(((TimeLog) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TimeLog{" +
            "id=" + id +
            ", timestamp='" + timestamp + "'" +
            "}";
    }

    public TimeLog update() {
        return update(this);
    }

    public TimeLog persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static TimeLog update(TimeLog timeLog) {
        if (timeLog == null) {
            throw new IllegalArgumentException("timeLog can't be null");
        }
        var entity = TimeLog.<TimeLog>findById(timeLog.id);
        if (entity != null) {
            entity.timestamp = timeLog.timestamp;
            entity.member = timeLog.member;
            entity.timeCheckTask = timeLog.timeCheckTask;
        }
        return entity;
    }

    public static TimeLog persistOrUpdate(TimeLog timeLog) {
        if (timeLog == null) {
            throw new IllegalArgumentException("timeLog can't be null");
        }
        if (timeLog.id == null) {
            persist(timeLog);
            return timeLog;
        } else {
            return update(timeLog);
        }
    }


}
