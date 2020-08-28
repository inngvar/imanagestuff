package org.manage.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectMapperTest {

    private ProjectMapper projectMapper;

    @BeforeEach
    public void setUp() {
        projectMapper = new ProjectMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(projectMapper.fromId(id).id).isEqualTo(id);
        assertThat(projectMapper.fromId(null)).isNull();
    }
}
