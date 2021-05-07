package org.manage.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

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
    @Column(name = "date", nullable = false)
    public LocalDate date;

    @Column(name = "check_in")
    public ZonedDateTime checkIn;

    @Column(name = "check_out")
    public ZonedDateTime checkOut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    @NotNull
    @JsonbTransient
    public Member member;

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
            ", date='" + date + "'" +
            ", checkIn='" + checkIn + "'" +
            ", checkOut='" + checkOut + "'" +
            "}";
    }

    public TimeLog update() {
        return update(this);
    }

    public TimeLog persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Stream<TimeLog> getAllByDateBetween(LocalDate dateFrom, LocalDate dateTo) {
        return find("From TimeLog e WHERE e.date >= ?1 AND e.date<=?2", dateFrom, dateTo)
            .stream();
    }

    public static Stream<TimeLog> getAllByDateBetweenAndMember(LocalDate dateFrom, LocalDate dateTo, Member member) {
        return find("From TimeLog e WHERE e.member=?1 AND (e.date >= ?2 AND e.date<=?3)", member, dateFrom, dateTo)
            .stream();
    }

    public static TimeLog update(TimeLog timeLog) {
        if (timeLog == null) {
            throw new IllegalArgumentException("timeLog can't be null");
        }
        var entity = TimeLog.<TimeLog>findById(timeLog.id);
        if (entity != null) {
            entity.date = timeLog.date;
            entity.checkIn = timeLog.checkIn;
            entity.checkOut = timeLog.checkOut;
            entity.member = timeLog.member;
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
