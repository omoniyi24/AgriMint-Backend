package com.github.agrimint.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FederationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Federation.class);
        Federation federation1 = new Federation();
        federation1.setId(1L);
        Federation federation2 = new Federation();
        federation2.setId(federation1.getId());
        assertThat(federation1).isEqualTo(federation2);
        federation2.setId(2L);
        assertThat(federation1).isNotEqualTo(federation2);
        federation1.setId(null);
        assertThat(federation1).isNotEqualTo(federation2);
    }
}
