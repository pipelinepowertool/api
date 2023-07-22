package com.pipelinepowertool.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;

class MathUtilsTest {

    @Test
    void testCalculateRuntimeInHours() {
        assertEquals("0.0027777778", MathUtils.calculateRuntimeInHours(10L).toString());
    }

    @Test
    void testCalculateWatts() {
        assertEquals("0.1000000000", MathUtils.calculateWatts(BigDecimal.valueOf(1L), 10L).toString());
        assertEquals("1.0000000000", MathUtils.calculateWatts(BigDecimal.valueOf(1L), 1L).toString());
        assertEquals("1.0000000000", MathUtils.calculateWatts(BigDecimal.valueOf(10L), 10L).toString());
        assertEquals("0.5000000000", MathUtils.calculateWatts(BigDecimal.valueOf(5L), 10L).toString());
    }

    @Test
    void testCalculateWattsZero() {
        BigDecimal result = MathUtils.calculateWatts(BigDecimal.valueOf(1L), 0L);
        assertSame(BigDecimal.ZERO, result);
        assertEquals("0", result.toString());
    }

    @Test
    void testCalculateKwh() {
        assertEquals("0.0010000000", MathUtils.calculateKwh(BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateKwhUsed() {
        BigDecimal kwh = BigDecimal.valueOf(1L);
        BigDecimal result = MathUtils.calculateKwhUsed(kwh, BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ONE, result);
        assertEquals("1", result.toString());
    }

    @Test
    void testCalculateKwhUsed2() {
        BigDecimal kwh = BigDecimal.valueOf(5L);
        assertEquals("5", MathUtils.calculateKwhUsed(kwh, BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateKwhUsedZero() {
        BigDecimal kwh = BigDecimal.valueOf(0L);
        BigDecimal result = MathUtils.calculateKwhUsed(kwh, BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ZERO, result);
        assertEquals("0", result.toString());
    }

    @Test
    void testCalculateKwhUsedNegative() {
        BigDecimal kwh = BigDecimal.valueOf(-1L);
        assertEquals("-1", MathUtils.calculateKwhUsed(kwh, BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateKwhPerPipeline() {
        assertEquals("1.0000000000", MathUtils.calculateKwhPerPipeline(BigDecimal.valueOf(1L), 1L).toString());
        assertEquals("10.0000000000", MathUtils.calculateKwhPerPipeline(BigDecimal.valueOf(10L), 1L).toString());
        assertEquals("5.0000000000", MathUtils.calculateKwhPerPipeline(BigDecimal.valueOf(5L), 1L).toString());
        assertEquals("0E-10", MathUtils.calculateKwhPerPipeline(BigDecimal.valueOf(0L), 1L).toString());
    }

    @Test
    void testCalculateKwhPerPipelineZero() {
        BigDecimal result = MathUtils.calculateKwhPerPipeline(BigDecimal.valueOf(1L), 0L);
        assertSame(BigDecimal.ZERO, result);
        assertEquals("0", result.toString());
    }

    @Test
    void testCalculateCo2ProducedTotal() {
        BigDecimal kwhUsed = BigDecimal.valueOf(1L);
        BigDecimal result = MathUtils.calculateCo2ProducedTotal(kwhUsed,
                BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ONE, result);
        assertEquals("1", result.toString());
    }

    @Test
    void testCalculateCo2ProducedTotal2() {
        BigDecimal kwhUsed = BigDecimal.valueOf(5L);
        assertEquals("5", MathUtils.calculateCo2ProducedTotal(kwhUsed, BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateCo2ProducedTotalZero() {
        BigDecimal kwhUsed = BigDecimal.valueOf(0L);
        BigDecimal result = MathUtils.calculateCo2ProducedTotal(kwhUsed,
                BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ZERO, result);
        assertEquals("0", result.toString());
    }

    @Test
    void testCalculateCo2ProducedTotalNegative() {
        BigDecimal kwhUsed = BigDecimal.valueOf(-1L);
        assertEquals("-1", MathUtils.calculateCo2ProducedTotal(kwhUsed, BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateCo2ProducingPerRun() {
        BigDecimal kwhPerPipeline = BigDecimal.valueOf(1L);
        BigDecimal result = MathUtils.calculateCo2ProducingPerRun(kwhPerPipeline,
                BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ONE, result);
        assertEquals("1", result.toString());
    }

    @Test
    void testCalculateCo2ProducingPerRun2() {
        BigDecimal kwhPerPipeline = BigDecimal.valueOf(5L);
        assertEquals("5", MathUtils.calculateCo2ProducingPerRun(kwhPerPipeline, BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateCo2ProducingPerRunZero() {
        BigDecimal kwhPerPipeline = BigDecimal.valueOf(0L);
        BigDecimal result = MathUtils.calculateCo2ProducingPerRun(kwhPerPipeline,
                BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ZERO, result);
        assertEquals("0", result.toString());
    }

    @Test
    void testCalculateCo2ProducingPerRunNegative() {
        BigDecimal kwhPerPipeline = BigDecimal.valueOf(-1L);
        assertEquals("-1", MathUtils.calculateCo2ProducingPerRun(kwhPerPipeline, BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateCo2ProducingPerHour() {
        BigDecimal kwh = BigDecimal.valueOf(1L);
        BigDecimal result = MathUtils.calculateCo2ProducingPerHour(kwh,
                BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ONE, result);
        assertEquals("1", result.toString());
    }

    @Test
    void testCalculateCo2ProducingPerHour2() {
        BigDecimal kwh = BigDecimal.valueOf(5L);
        assertEquals("5", MathUtils.calculateCo2ProducingPerHour(kwh, BigDecimal.valueOf(1L)).toString());
    }

    @Test
    void testCalculateCo2ProducingPerHourZero() {
        BigDecimal kwh = BigDecimal.valueOf(0L);
        BigDecimal result = MathUtils.calculateCo2ProducingPerHour(kwh,
                BigDecimal.valueOf(1L));
        assertSame(BigDecimal.ZERO, result);
        assertEquals("0", result.toString());
    }

    @Test
    void testCalculateCo2ProducingPerHourNegative() {
        BigDecimal kwh = BigDecimal.valueOf(-1L);
        assertEquals("-1", MathUtils.calculateCo2ProducingPerHour(kwh, BigDecimal.valueOf(1L)).toString());
    }

}

