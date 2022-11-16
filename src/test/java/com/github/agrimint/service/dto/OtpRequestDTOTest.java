package com.github.agrimint.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OtpRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OtpRequestDTO.class);
        OtpRequestDTO otpRequestDTO1 = new OtpRequestDTO();
        otpRequestDTO1.setId(1L);
        OtpRequestDTO otpRequestDTO2 = new OtpRequestDTO();
        assertThat(otpRequestDTO1).isNotEqualTo(otpRequestDTO2);
        otpRequestDTO2.setId(otpRequestDTO1.getId());
        assertThat(otpRequestDTO1).isEqualTo(otpRequestDTO2);
        otpRequestDTO2.setId(2L);
        assertThat(otpRequestDTO1).isNotEqualTo(otpRequestDTO2);
        otpRequestDTO1.setId(null);
        assertThat(otpRequestDTO1).isNotEqualTo(otpRequestDTO2);
    }
}
