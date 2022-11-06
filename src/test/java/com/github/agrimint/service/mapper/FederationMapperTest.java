package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FederationMapperTest {

    private FederationMapper federationMapper;

    @BeforeEach
    public void setUp() {
        federationMapper = new FederationMapperImpl();
    }
}
