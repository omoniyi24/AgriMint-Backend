package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberMapperTest {

    private MemberMapper memberMapper;

    @BeforeEach
    public void setUp() {
        memberMapper = new MemberMapperImpl();
    }
}
