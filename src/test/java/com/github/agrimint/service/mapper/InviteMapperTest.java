package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InviteMapperTest {

    private InviteMapper inviteMapper;

    @BeforeEach
    public void setUp() {
        inviteMapper = new InviteMapperImpl();
    }
}
