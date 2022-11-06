package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FederationMemberMapperTest {

    private FederationMemberMapper federationMemberMapper;

    @BeforeEach
    public void setUp() {
        federationMemberMapper = new FederationMemberMapperImpl();
    }
}
