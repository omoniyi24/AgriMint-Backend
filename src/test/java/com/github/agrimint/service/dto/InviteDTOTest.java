package com.github.agrimint.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InviteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InviteDTO.class);
        InviteDTO inviteDTO1 = new InviteDTO();
        inviteDTO1.setId(1L);
        InviteDTO inviteDTO2 = new InviteDTO();
        assertThat(inviteDTO1).isNotEqualTo(inviteDTO2);
        inviteDTO2.setId(inviteDTO1.getId());
        assertThat(inviteDTO1).isEqualTo(inviteDTO2);
        inviteDTO2.setId(2L);
        assertThat(inviteDTO1).isNotEqualTo(inviteDTO2);
        inviteDTO1.setId(null);
        assertThat(inviteDTO1).isNotEqualTo(inviteDTO2);
    }
}
