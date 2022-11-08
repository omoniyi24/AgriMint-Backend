package com.github.agrimint.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PrivilegeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrivilegeDTO.class);
        PrivilegeDTO privilegeDTO1 = new PrivilegeDTO();
        privilegeDTO1.setId(1L);
        PrivilegeDTO privilegeDTO2 = new PrivilegeDTO();
        assertThat(privilegeDTO1).isNotEqualTo(privilegeDTO2);
        privilegeDTO2.setId(privilegeDTO1.getId());
        assertThat(privilegeDTO1).isEqualTo(privilegeDTO2);
        privilegeDTO2.setId(2L);
        assertThat(privilegeDTO1).isNotEqualTo(privilegeDTO2);
        privilegeDTO1.setId(null);
        assertThat(privilegeDTO1).isNotEqualTo(privilegeDTO2);
    }
}
