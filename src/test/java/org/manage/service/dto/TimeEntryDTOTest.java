package org.manage.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.manage.TestUtil;

public class TimeEntryDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeEntryDTO.class);
        TimeEntryDTO timeEntryDTO1 = new TimeEntryDTO();
        timeEntryDTO1.id = 1L;
        TimeEntryDTO timeEntryDTO2 = new TimeEntryDTO();
        assertThat(timeEntryDTO1).isNotEqualTo(timeEntryDTO2);
        timeEntryDTO2.id = timeEntryDTO1.id;
        assertThat(timeEntryDTO1).isEqualTo(timeEntryDTO2);
        timeEntryDTO2.id = 2L;
        assertThat(timeEntryDTO1).isNotEqualTo(timeEntryDTO2);
        timeEntryDTO1.id = null;
        assertThat(timeEntryDTO1).isNotEqualTo(timeEntryDTO2);
    }
}
