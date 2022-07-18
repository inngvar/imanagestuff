package org.manage.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.manage.domain.enums.DayType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "day_info")
@RegisterForReflection
public class DayInfo extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name="source_type")
    public String sourceType;

    @Column(name="t_day")
    public LocalDate day;

    @Column(name="day_type")
    public DayType dayType;

    public static Optional<DayInfo> getByDate(LocalDate date, String consultantSourceType){
        return find("FROM DayInfo d WHERE d.day=?1 AND d.sourceType=?2", date, consultantSourceType).firstResultOptional();
    }

}
