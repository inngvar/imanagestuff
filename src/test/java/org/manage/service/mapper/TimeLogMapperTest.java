package org.manage.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeLogMapperTest {

    private TimeLogMapper timeLogMapper;

    @BeforeEach
    public void setUp() {
        timeLogMapper = new TimeLogMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(timeLogMapper.fromId(id).id).isEqualTo(id);
        assertThat(timeLogMapper.fromId(null)).isNull();
    }
}
