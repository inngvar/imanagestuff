package org.manage.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.manage.TestUtil;

public class TimeLogDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeLogDTO.class);
        TimeLogDTO timeLogDTO1 = new TimeLogDTO();
        timeLogDTO1.id = 1L;
        TimeLogDTO timeLogDTO2 = new TimeLogDTO();
        assertThat(timeLogDTO1).isNotEqualTo(timeLogDTO2);
        timeLogDTO2.id = timeLogDTO1.id;
        assertThat(timeLogDTO1).isEqualTo(timeLogDTO2);
        timeLogDTO2.id = 2L;
        assertThat(timeLogDTO1).isNotEqualTo(timeLogDTO2);
        timeLogDTO1.id = null;
        assertThat(timeLogDTO1).isNotEqualTo(timeLogDTO2);
    }
}
