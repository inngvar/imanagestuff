package org.manage.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeEntryMapperTest {

    private TimeEntryMapper timeEntryMapper;

    @BeforeEach
    public void setUp() {
        timeEntryMapper = new TimeEntryMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(timeEntryMapper.fromId(id).id).isEqualTo(id);
        assertThat(timeEntryMapper.fromId(null)).isNull();
    }
}
