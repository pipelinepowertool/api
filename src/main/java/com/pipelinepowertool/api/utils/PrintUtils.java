package com.pipelinepowertool.api.utils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PrintUtils {

    public static String prettyPrintBigDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(5, RoundingMode.CEILING).stripTrailingZeros().toPlainString();
    }

    public static String prettyPrintUtilization(@Nonnull BigDecimal utilization) {
        return utilization.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
