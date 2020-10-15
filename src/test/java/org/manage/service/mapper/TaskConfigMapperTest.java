package org.manage.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskConfigMapperTest {

    private TaskConfigMapper taskConfigMapper;

    @BeforeEach
    public void setUp() {
        taskConfigMapper = new TaskConfigMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(taskConfigMapper.fromId(id).id).isEqualTo(id);
        assertThat(taskConfigMapper.fromId(null)).isNull();
    }
}
