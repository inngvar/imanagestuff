package org.manage.domain;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.json.bind.annotation.JsonbTransient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
@RegisterForReflection
public class Project extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    public String name;

    @Size(max = 4000)
    @Column(name = "descrption", length = 4000)
    public String descrption;

    @Size(max = 4000)
    @Column(name = "send_reports", length = 4000)
    public String sendReports;

    @ManyToMany
    @JoinTable(name = "project_members",
               joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "members_id", referencedColumnName = "id"))
    @JsonbTransient
    public Set<Member> members = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", descrption='" + descrption + "'" +
            ", sendReports='" + sendReports + "'" +
            "}";
    }

    public Project update() {
        return update(this);
    }

    public Project persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Project update(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("project can't be null");
        }
        var entity = Project.<Project>findById(project.id);
        if (entity != null) {
            entity.name = project.name;
            entity.descrption = project.descrption;
            entity.sendReports = project.sendReports;
            entity.members = project.members;
        }
        return entity;
    }

    public static Project persistOrUpdate(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("project can't be null");
        }
        if (project.id == null) {
            persist(project);
            return project;
        } else {
            return update(project);
        }
    }

    public static PanacheQuery<Project> findAllWithEagerRelationships() {
        return find("select distinct project from Project project left join fetch project.members");
    }

    public static Optional<Project> findOneWithEagerRelationships(Long id) {
        return find("select project from Project project left join fetch project.members where project.id =?1", id).firstResultOptional();
    }

}
