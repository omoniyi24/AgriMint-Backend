package com.github.agrimint.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.agrimint.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OtpRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OtpRequest.class);
        OtpRequest otpRequest1 = new OtpRequest();
        otpRequest1.setId(1L);
        OtpRequest otpRequest2 = new OtpRequest();
        otpRequest2.setId(otpRequest1.getId());
        assertThat(otpRequest1).isEqualTo(otpRequest2);
        otpRequest2.setId(2L);
        assertThat(otpRequest1).isNotEqualTo(otpRequest2);
        otpRequest1.setId(null);
        assertThat(otpRequest1).isNotEqualTo(otpRequest2);
    }
}
