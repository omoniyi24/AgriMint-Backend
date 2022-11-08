package com.github.agrimint.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PrivilegeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Privilege.class);
        Privilege privilege1 = new Privilege();
        privilege1.setId(1L);
        Privilege privilege2 = new Privilege();
        privilege2.setId(privilege1.getId());
        assertThat(privilege1).isEqualTo(privilege2);
        privilege2.setId(2L);
        assertThat(privilege1).isNotEqualTo(privilege2);
        privilege1.setId(null);
        assertThat(privilege1).isNotEqualTo(privilege2);
    }
}
