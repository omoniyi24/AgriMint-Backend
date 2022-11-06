package com.github.agrimint.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FederationMemberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FederationMemberDTO.class);
        FederationMemberDTO federationMemberDTO1 = new FederationMemberDTO();
        federationMemberDTO1.setId(1L);
        FederationMemberDTO federationMemberDTO2 = new FederationMemberDTO();
        assertThat(federationMemberDTO1).isNotEqualTo(federationMemberDTO2);
        federationMemberDTO2.setId(federationMemberDTO1.getId());
        assertThat(federationMemberDTO1).isEqualTo(federationMemberDTO2);
        federationMemberDTO2.setId(2L);
        assertThat(federationMemberDTO1).isNotEqualTo(federationMemberDTO2);
        federationMemberDTO1.setId(null);
        assertThat(federationMemberDTO1).isNotEqualTo(federationMemberDTO2);
    }
}
