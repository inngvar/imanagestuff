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

/**
 * A TaskConfig.
 */
@Entity
@Table(name = "task_config")
@RegisterForReflection
public class TaskConfig extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    public String name;

    @OneToMany(mappedBy = "taskConfig")
    public Set<Member> members = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskConfig)) {
            return false;
        }
        return id != null && id.equals(((TaskConfig) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TaskConfig{" +
            "id=" + id +
            ", name='" + name + "'" +
            "}";
    }

    public TaskConfig update() {
        return update(this);
    }

    public TaskConfig persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static TaskConfig update(TaskConfig taskConfig) {
        if (taskConfig == null) {
            throw new IllegalArgumentException("taskConfig can't be null");
        }
        var entity = TaskConfig.<TaskConfig>findById(taskConfig.id);
        if (entity != null) {
            entity.name = taskConfig.name;
            entity.members = taskConfig.members;
        }
        return entity;
    }

    public static TaskConfig persistOrUpdate(TaskConfig taskConfig) {
        if (taskConfig == null) {
            throw new IllegalArgumentException("taskConfig can't be null");
        }
        if (taskConfig.id == null) {
            persist(taskConfig);
            return taskConfig;
        } else {
            return update(taskConfig);
        }
    }


}
