package com.github.agrimint.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FederationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FederationDTO.class);
        FederationDTO federationDTO1 = new FederationDTO();
        federationDTO1.setId(1L);
        FederationDTO federationDTO2 = new FederationDTO();
        assertThat(federationDTO1).isNotEqualTo(federationDTO2);
        federationDTO2.setId(federationDTO1.getId());
        assertThat(federationDTO1).isEqualTo(federationDTO2);
        federationDTO2.setId(2L);
        assertThat(federationDTO1).isNotEqualTo(federationDTO2);
        federationDTO1.setId(null);
        assertThat(federationDTO1).isNotEqualTo(federationDTO2);
    }
}
