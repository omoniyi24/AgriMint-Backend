package com.github.agrimint.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GuardianTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Guardian.class);
        Guardian guardian1 = new Guardian();
        guardian1.setId(1L);
        Guardian guardian2 = new Guardian();
        guardian2.setId(guardian1.getId());
        assertThat(guardian1).isEqualTo(guardian2);
        guardian2.setId(2L);
        assertThat(guardian1).isNotEqualTo(guardian2);
        guardian1.setId(null);
        assertThat(guardian1).isNotEqualTo(guardian2);
    }
}
