package com.github.agrimint.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionsMapperTest {

    private TransactionsMapper transactionsMapper;

    @BeforeEach
    public void setUp() {
        transactionsMapper = new TransactionsMapperImpl();
    }
}
