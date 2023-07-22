package com.pipelinepowertool.api.calculator;


import com.pipelinepowertool.api.model.PowerUsageResponse;
import com.pipelinepowertool.api.utils.MathUtils;
import com.pipelinepowertool.common.core.database.elasticsearch.ElasticSearchService;
import com.pipelinepowertool.common.core.database.models.DatabaseAggregationResponse;
import com.pipelinepowertool.common.core.pipeline.jenkins.JenkinsMetadata;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.pipelinepowertool.api.utils.PrintUtils.prettyPrintBigDecimal;
import static com.pipelinepowertool.api.utils.PrintUtils.prettyPrintUtilization;

@ApplicationScoped
public class CalculatorService {

    private static final Logger LOG = Logger.getLogger(CalculatorService.class);
    @Inject
    ElasticSearchService elasticSearchService;

    @Inject
    CarbonIntensityService carbonIntensityService;

    public Uni<PowerUsageResponse> getPowerUsage(JenkinsMetadata jenkinsMetadata, String countryCode, Float price) {
        CompletableFuture<PowerUsageResponse> powerUsageResponseInFuture = elasticSearchService.aggregate(jenkinsMetadata).
                thenApply(aggregate -> createPowerUsageResponse(jenkinsMetadata, aggregate, countryCode, price))
                .whenComplete((r, t) -> {
                    if (t != null) {
                        LOG.error("Something went wrong", t);
                    }
                });
        return Uni.createFrom().completionStage(powerUsageResponseInFuture);
    }

    private PowerUsageResponse createPowerUsageResponse(JenkinsMetadata jenkinsMetadata, DatabaseAggregationResponse databaseAggregationResponse, String countryCode, Float price) {
        long pipelineRuns = databaseAggregationResponse.getPipelineRuns();
        long runtimeInSeconds = databaseAggregationResponse.getRuntime();
        BigDecimal utilization = databaseAggregationResponse.getUtilization();
        BigDecimal joules = databaseAggregationResponse.getJoules();

        BigDecimal runtimeInHours = MathUtils.calculateRuntimeInHours(runtimeInSeconds);
        BigDecimal watts = MathUtils.calculateWatts(joules, runtimeInSeconds);
        BigDecimal kwh = MathUtils.calculateKwh(watts);
        BigDecimal kwhUsed = MathUtils.calculateKwhUsed(kwh, runtimeInHours);

        BigDecimal carbonIntensity = BigDecimal.valueOf(carbonIntensityService.getCarbonIntensity(countryCode));
        BigDecimal co2ProducedTotalGr = MathUtils.calculateCo2ProducedTotal(kwhUsed, carbonIntensity);

        PowerUsageResponse powerUsageResponse = new PowerUsageResponse()
                .powerUsageKwh(prettyPrintBigDecimal(kwhUsed))
                .co2ProducedTotalGr(prettyPrintBigDecimal(co2ProducedTotalGr))
                .cpuUtilization(prettyPrintUtilization(utilization))
                .pipelineRuns(String.valueOf(pipelineRuns))
                .runtimeSeconds(String.valueOf(runtimeInSeconds));

        if (price != null) {
            BigDecimal priceBigDecimal = BigDecimal.valueOf(price);
            BigDecimal priceTotal = MathUtils.calculatePriceOfKwh(co2ProducedTotalGr, priceBigDecimal);
            powerUsageResponse.priceTotal(prettyPrintBigDecimal(priceTotal));
        }

        if (jenkinsMetadata.getJob() != null) {
            BigDecimal kwhPerPipeline = MathUtils.calculateKwhPerPipeline(kwhUsed, pipelineRuns);
            BigDecimal co2ProducingPerRunGr = MathUtils.calculateCo2ProducingPerRun(kwhPerPipeline, carbonIntensity);
            BigDecimal co2ProducingPerHourGr = MathUtils.calculateCo2ProducingPerHour(kwh, carbonIntensity);
            powerUsageResponse = powerUsageResponse
                    .co2ProducingPerRunGr(prettyPrintBigDecimal(co2ProducingPerRunGr))
                    .co2ProducingPerHourGr(prettyPrintBigDecimal(co2ProducingPerHourGr));
            if (price != null) {
                BigDecimal priceBigDecimal = BigDecimal.valueOf(price);
                BigDecimal pricePerRun = MathUtils.calculatePriceOfKwh(kwhPerPipeline, priceBigDecimal);
                BigDecimal pricePerHour = MathUtils.calculatePriceOfKwh(kwh, priceBigDecimal);
                powerUsageResponse.pricePerRun(prettyPrintBigDecimal(pricePerRun))
                        .pricePerHour(prettyPrintBigDecimal(pricePerHour));
            }
        }

        return powerUsageResponse;


    }


}
