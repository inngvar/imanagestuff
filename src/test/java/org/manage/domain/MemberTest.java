package org.manage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.manage.TestUtil;
import org.junit.jupiter.api.Test;


public class MemberTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Member.class);
        Member member1 = new Member();
        member1.id = 1L;
        Member member2 = new Member();
        member2.id = member1.id;
        assertThat(member1).isEqualTo(member2);
        member2.id = 2L;
        assertThat(member1).isNotEqualTo(member2);
        member1.id = null;
        assertThat(member1).isNotEqualTo(member2);
    }
}
