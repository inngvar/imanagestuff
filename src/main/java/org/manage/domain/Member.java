package org.manage.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbTransient;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.manage.service.dto.MemberDTO;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A Member.
 */
@Entity
@Table(name = "member")
@RegisterForReflection
public class Member extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @NotNull
    @Column(name = "login", nullable = false, unique = true)
    public String login;

    @NotNull
    @Column(name = "first_name", nullable = false)
    public String firstName;

    @Column(name = "middle_name")
    public String middleName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    public String lastName;

    @OneToOne
    @JoinColumn
    public Project defaultProject;

    @Column(name = "telegram_id", unique = true)
    public Long telegramId;

    @OneToMany(mappedBy = "member")
    public Set<TimeLog> timeLogs = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @JsonbTransient
    public Set<Project> projects = new HashSet<>();

    public static Optional<Member> findByLogin(String login) {
        return find("login=?1", login).firstResultOptional();
    }

    public static Optional<Member> findByTelegramId(Long telegramId) {
        return find("telegramId=?1", telegramId).firstResultOptional();
    }

    public static List<Member> findAllWhoNeedReminder(LocalDate date) {
        return find("telegramId IS NOT NULL AND NOT EXISTS (FROM TimeEntry te WHERE te.member.id = id AND te.date = ?1)", date).list();
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        return id != null && id.equals(((Member) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Member{" +
            "id=" + id +
            ", login='" + login + "'" +
            ", firstName='" + firstName + "'" +
            ", middleName='" + middleName + "'" +
            ", lastName='" + lastName + "'" +
            "}";
    }

    public Member update() {
        return update(this);
    }

    public Member persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Member update(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("member can't be null");
        }
        var entity = Member.<Member>findById(member.id);
        if (entity != null) {
            entity.login = member.login;
            entity.firstName = member.firstName;
            entity.middleName = member.middleName;
            entity.lastName = member.lastName;
            entity.defaultProject = member.defaultProject;
            entity.telegramId = member.telegramId;
            entity.timeLogs = member.timeLogs;
            entity.projects = member.projects;
        }
        return entity;
    }

    public static Member persistOrUpdate(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("member can't be null");
        }
        if (member.id == null) {
            persist(member);
            return member;
        } else {
            return update(member);
        }
    }


}
