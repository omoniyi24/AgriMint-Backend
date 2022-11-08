package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrivilegeMapperTest {

    private PrivilegeMapper privilegeMapper;

    @BeforeEach
    public void setUp() {
        privilegeMapper = new PrivilegeMapperImpl();
    }
}
