package org.manage.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.manage.TestUtil;

public class TimeCheckTaskDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeCheckTaskDTO.class);
        TimeCheckTaskDTO timeCheckTaskDTO1 = new TimeCheckTaskDTO();
        timeCheckTaskDTO1.id = 1L;
        TimeCheckTaskDTO timeCheckTaskDTO2 = new TimeCheckTaskDTO();
        assertThat(timeCheckTaskDTO1).isNotEqualTo(timeCheckTaskDTO2);
        timeCheckTaskDTO2.id = timeCheckTaskDTO1.id;
        assertThat(timeCheckTaskDTO1).isEqualTo(timeCheckTaskDTO2);
        timeCheckTaskDTO2.id = 2L;
        assertThat(timeCheckTaskDTO1).isNotEqualTo(timeCheckTaskDTO2);
        timeCheckTaskDTO1.id = null;
        assertThat(timeCheckTaskDTO1).isNotEqualTo(timeCheckTaskDTO2);
    }
}
