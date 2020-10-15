package org.manage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.manage.TestUtil;
import org.junit.jupiter.api.Test;


public class TimeLogTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeLog.class);
        TimeLog timeLog1 = new TimeLog();
        timeLog1.id = 1L;
        TimeLog timeLog2 = new TimeLog();
        timeLog2.id = timeLog1.id;
        assertThat(timeLog1).isEqualTo(timeLog2);
        timeLog2.id = 2L;
        assertThat(timeLog1).isNotEqualTo(timeLog2);
        timeLog1.id = null;
        assertThat(timeLog1).isNotEqualTo(timeLog2);
    }
}
