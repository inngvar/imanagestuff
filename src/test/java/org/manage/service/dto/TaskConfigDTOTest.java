package org.manage.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.manage.TestUtil;

public class TaskConfigDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskConfigDTO.class);
        TaskConfigDTO taskConfigDTO1 = new TaskConfigDTO();
        taskConfigDTO1.id = 1L;
        TaskConfigDTO taskConfigDTO2 = new TaskConfigDTO();
        assertThat(taskConfigDTO1).isNotEqualTo(taskConfigDTO2);
        taskConfigDTO2.id = taskConfigDTO1.id;
        assertThat(taskConfigDTO1).isEqualTo(taskConfigDTO2);
        taskConfigDTO2.id = 2L;
        assertThat(taskConfigDTO1).isNotEqualTo(taskConfigDTO2);
        taskConfigDTO1.id = null;
        assertThat(taskConfigDTO1).isNotEqualTo(taskConfigDTO2);
    }
}
