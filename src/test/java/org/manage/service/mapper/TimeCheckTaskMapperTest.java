package org.manage.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeCheckTaskMapperTest {

    private TimeCheckTaskMapper timeCheckTaskMapper;

    @BeforeEach
    public void setUp() {
        timeCheckTaskMapper = new TimeCheckTaskMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(timeCheckTaskMapper.fromId(id).id).isEqualTo(id);
        assertThat(timeCheckTaskMapper.fromId(null)).isNull();
    }
}
