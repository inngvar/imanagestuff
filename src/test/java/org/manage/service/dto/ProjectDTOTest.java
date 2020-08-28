package org.manage.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.manage.TestUtil;

public class ProjectDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectDTO.class);
        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.id = 1L;
        ProjectDTO projectDTO2 = new ProjectDTO();
        assertThat(projectDTO1).isNotEqualTo(projectDTO2);
        projectDTO2.id = projectDTO1.id;
        assertThat(projectDTO1).isEqualTo(projectDTO2);
        projectDTO2.id = 2L;
        assertThat(projectDTO1).isNotEqualTo(projectDTO2);
        projectDTO1.id = null;
        assertThat(projectDTO1).isNotEqualTo(projectDTO2);
    }
}
