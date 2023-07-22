package com.pipelinepowertool.api.utils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    private static final BigDecimal WATT_TO_KWH_DIVIDER = BigDecimal.valueOf(1000);

    public static BigDecimal calculateRuntimeInHours(long runtimeInSeconds) {
        return divide(BigDecimal.valueOf(runtimeInSeconds), BigDecimal.valueOf(3600));
    }

    public static BigDecimal calculateWatts(@Nonnull BigDecimal joules, long runtimeInSeconds) {
        return divide(joules, BigDecimal.valueOf(runtimeInSeconds));
    }

    public static BigDecimal calculateKwh(@Nonnull BigDecimal watts) {
        return divide(watts, WATT_TO_KWH_DIVIDER);
    }

    public static BigDecimal calculateKwhUsed(@Nonnull BigDecimal kwh, @Nonnull BigDecimal runtimeInHours) {
        return kwh.multiply(runtimeInHours);
    }

    public static BigDecimal calculateKwhPerPipeline(@Nonnull BigDecimal kwhUsed, long pipelineRuns) {
        return divide(kwhUsed, BigDecimal.valueOf(pipelineRuns));
    }

    public static BigDecimal calculateCo2ProducedTotal(@Nonnull BigDecimal kwhUsed, @Nonnull BigDecimal carbonIntensityGr) {
        return carbonIntensityGr.multiply(kwhUsed);
    }

    public static BigDecimal calculatePriceOfKwh(@Nonnull BigDecimal kwhUsed, BigDecimal price) {
        if (price == null) {
            return null;
        }
        return kwhUsed.multiply(price);
    }

    public static BigDecimal calculateCo2ProducingPerRun(@Nonnull BigDecimal kwhPerPipeline, @Nonnull BigDecimal carbonIntensity) {
        return kwhPerPipeline.multiply(carbonIntensity);
    }

    public static BigDecimal calculateCo2ProducingPerHour(@Nonnull BigDecimal kwh, @Nonnull BigDecimal carbonIntensity) {
        return kwh.multiply(carbonIntensity);
    }

    private static BigDecimal divide(BigDecimal one, BigDecimal two) {
        if (two.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return one.divide(two, 10, RoundingMode.HALF_UP);
    }

}
