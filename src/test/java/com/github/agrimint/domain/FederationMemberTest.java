package com.github.agrimint.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FederationMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FederationMember.class);
        FederationMember federationMember1 = new FederationMember();
        federationMember1.setId(1L);
        FederationMember federationMember2 = new FederationMember();
        federationMember2.setId(federationMember1.getId());
        assertThat(federationMember1).isEqualTo(federationMember2);
        federationMember2.setId(2L);
        assertThat(federationMember1).isNotEqualTo(federationMember2);
        federationMember1.setId(null);
        assertThat(federationMember1).isNotEqualTo(federationMember2);
    }
}
