package org.manage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.manage.TestUtil;
import org.junit.jupiter.api.Test;


public class TimeEntryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeEntry.class);
        TimeEntry timeEntry1 = new TimeEntry();
        timeEntry1.id = 1L;
        TimeEntry timeEntry2 = new TimeEntry();
        timeEntry2.id = timeEntry1.id;
        assertThat(timeEntry1).isEqualTo(timeEntry2);
        timeEntry2.id = 2L;
        assertThat(timeEntry1).isNotEqualTo(timeEntry2);
        timeEntry1.id = null;
        assertThat(timeEntry1).isNotEqualTo(timeEntry2);
    }
}
