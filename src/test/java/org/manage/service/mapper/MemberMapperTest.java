package org.manage.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MemberMapperTest {

    private MemberMapper memberMapper;

    @BeforeEach
    public void setUp() {
        memberMapper = new MemberMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(memberMapper.fromId(id).id).isEqualTo(id);
        assertThat(memberMapper.fromId(null)).isNull();
    }
}
