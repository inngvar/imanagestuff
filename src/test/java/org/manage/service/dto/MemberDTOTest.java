package org.manage.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.manage.TestUtil;

public class MemberDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MemberDTO.class);
        MemberDTO memberDTO1 = new MemberDTO();
        memberDTO1.id = 1L;
        MemberDTO memberDTO2 = new MemberDTO();
        assertThat(memberDTO1).isNotEqualTo(memberDTO2);
        memberDTO2.id = memberDTO1.id;
        assertThat(memberDTO1).isEqualTo(memberDTO2);
        memberDTO2.id = 2L;
        assertThat(memberDTO1).isNotEqualTo(memberDTO2);
        memberDTO1.id = null;
        assertThat(memberDTO1).isNotEqualTo(memberDTO2);
    }
}
