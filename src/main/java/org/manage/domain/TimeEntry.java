package org.manage.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbTransient;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

/**
 * A TimeEntry.
 */
@Entity
@Table(name = "time_entry")
@RegisterForReflection
public class TimeEntry extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @NotNull
    @Column(name = "duration", nullable = false)
    public Duration duration;

    @NotNull
    @Column(name = "date", nullable = false)
    public LocalDate date;

    @Size(max = 256)
    @Column(name = "short_description", length = 256)
    public String shortDescription;

    @Size(max = 4000)
    @Column(name = "description", length = 4000)
    public String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    @NotNull
    @JsonbTransient
    public Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    @NotNull
    @JsonbTransient
    public Project project;

    public static Stream<TimeEntry> getAllByDateBetweenAndMemberAndProject(LocalDate dateFrom, LocalDate dateTo, Member member, Project project) {
        return find("From TimeEntry e WHERE e.member=?1 AND e.project = ?2 AND (e.date >= ?3 AND e.date<=?4)", member, project, dateFrom, dateTo)
            .stream();
    }

    public static List<TimeEntry> getAllByDateBetweenAndMember(LocalDate dateFrom, LocalDate dateTo, Member member) {
        return find("From TimeEntry e WHERE e.member=?1 AND (e.date >= ?2 AND e.date<=?3)", member, dateFrom, dateTo).list();
    }


    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeEntry)) {
            return false;
        }
        return id != null && id.equals(((TimeEntry) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TimeEntry{" +
            "id=" + id +
            ", duration='" + duration + "'" +
            ", date='" + date + "'" +
            ", shortDescription='" + shortDescription + "'" +
            ", description='" + description + "'" +
            "}";
    }

    public TimeEntry update() {
        return update(this);
    }

    public TimeEntry persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static TimeEntry update(TimeEntry timeEntry) {
        if (timeEntry == null) {
            throw new IllegalArgumentException("timeEntry can't be null");
        }
        var entity = TimeEntry.<TimeEntry>findById(timeEntry.id);
        if (entity != null) {
            entity.duration = timeEntry.duration;
            entity.date = timeEntry.date;
            entity.shortDescription = timeEntry.shortDescription;
            entity.description = timeEntry.description;
            entity.member = timeEntry.member;
            entity.project = timeEntry.project;
        }
        return entity;
    }

    public static TimeEntry persistOrUpdate(TimeEntry timeEntry) {
        if (timeEntry == null) {
            throw new IllegalArgumentException("timeEntry can't be null");
        }
        if (timeEntry.id == null) {
            persist(timeEntry);
            return timeEntry;
        } else {
            return update(timeEntry);
        }
    }


}
