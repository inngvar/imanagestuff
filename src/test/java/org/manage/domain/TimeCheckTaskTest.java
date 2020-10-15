package org.manage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.manage.TestUtil;
import org.junit.jupiter.api.Test;


public class TimeCheckTaskTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeCheckTask.class);
        TimeCheckTask timeCheckTask1 = new TimeCheckTask();
        timeCheckTask1.id = 1L;
        TimeCheckTask timeCheckTask2 = new TimeCheckTask();
        timeCheckTask2.id = timeCheckTask1.id;
        assertThat(timeCheckTask1).isEqualTo(timeCheckTask2);
        timeCheckTask2.id = 2L;
        assertThat(timeCheckTask1).isNotEqualTo(timeCheckTask2);
        timeCheckTask1.id = null;
        assertThat(timeCheckTask1).isNotEqualTo(timeCheckTask2);
    }
}
