package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuardianMapperTest {

    private GuardianMapper guardianMapper;

    @BeforeEach
    public void setUp() {
        guardianMapper = new GuardianMapperImpl();
    }
}
