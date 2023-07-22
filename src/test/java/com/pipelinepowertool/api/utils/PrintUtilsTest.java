package com.pipelinepowertool.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;

class PrintUtilsTest {

    @Test
    void testPrettyPrintBigDecimal() {
        assertEquals("1", PrintUtils.prettyPrintBigDecimal(BigDecimal.valueOf(1L)));
    }

    @Test
    void testPrettyPrintUtilization() {
        assertEquals("1.00", PrintUtils.prettyPrintUtilization(BigDecimal.valueOf(1L)));
    }
}

