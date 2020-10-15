package org.manage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.manage.TestUtil;
import org.junit.jupiter.api.Test;


public class TaskConfigTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskConfig.class);
        TaskConfig taskConfig1 = new TaskConfig();
        taskConfig1.id = 1L;
        TaskConfig taskConfig2 = new TaskConfig();
        taskConfig2.id = taskConfig1.id;
        assertThat(taskConfig1).isEqualTo(taskConfig2);
        taskConfig2.id = 2L;
        assertThat(taskConfig1).isNotEqualTo(taskConfig2);
        taskConfig1.id = null;
        assertThat(taskConfig1).isNotEqualTo(taskConfig2);
    }
}
