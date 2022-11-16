package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OtpRequestMapperTest {

    private OtpRequestMapper otpRequestMapper;

    @BeforeEach
    public void setUp() {
        otpRequestMapper = new OtpRequestMapperImpl();
    }
}
