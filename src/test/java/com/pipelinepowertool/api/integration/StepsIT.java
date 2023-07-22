package com.pipelinepowertool.api.integration;

import com.pipelinepowertool.api.utils.MathUtils;
import com.pipelinepowertool.common.core.pipeline.jenkins.JenkinsMetadata;
import com.pipelinepowertool.common.core.pipeline.jenkins.JenkinsMetadataBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.pipelinepowertool.api.utils.Constants.DEFAULT_CARBON_INTENSITY;
import static com.pipelinepowertool.api.utils.DataConstants.*;
import static com.pipelinepowertool.api.utils.PrintUtils.prettyPrintBigDecimal;
import static com.pipelinepowertool.api.utils.PrintUtils.prettyPrintUtilization;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StepsIT {

    @Inject
    CucumberContextHolder context;

    StringBuilder uriBuilder = new StringBuilder("calculator/jenkins");

    private JenkinsMetadataBuilder metadataBuilder = new JenkinsMetadataBuilder();

    @Given("User wants to see the carbon footprint of all Jenkins pipelines")
    public void userWantsToSeeTheCarbonFootprintOfAllJenkinsPipelines() {
        //Do nothing as it's an empty request
    }

    @When("User requests the data")
    public void userRequestsTheData() {
        String uri = uriBuilder.toString();
        context.response = given()
                .config(config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .pathParams(getQueryParamsFromMetaBuilder())
                .accept(ContentType.JSON)
                .when()
                .get(uriBuilder.toString());
        metadataBuilder = new JenkinsMetadataBuilder();
        uriBuilder = new StringBuilder("calculator/jenkins");
    }

    @Then("Request is successful")
    public void requestIsSuccessful() {
        context.response.then().assertThat().statusCode(OK);
    }

    @And("User sees the carbon footprint of all pipelines")
    public void userSeesTheCarbonFootprintOfAllPipelines() {
        int pipelineRuns = AMOUNT_OF_PIPELINES * AMOUNT_OF_BRANCHES * AMOUNT_OF_BUILDS;
        validateResponseAll(pipelineRuns, null);
    }

    @Given("User wants to see the carbon footprint of a specific Jenkins pipeline")
    public void userWantsToSeeTheCarbonFootprintOfASpecificJenkinsPipeline() {
        uriBuilder.append("?job=pipeline1");
    }

    @And("User sees the carbon footprint of a specific Jenkins pipeline")
    public void userSeesTheCarbonFootprintOfASpecificJenkinsPipeline() {
        int pipelineRuns = AMOUNT_OF_BRANCHES * AMOUNT_OF_BUILDS;
        validateResponseWithFilter(pipelineRuns, null);
    }

    @And("A specific build")
    public void aSpecificBuild() {
        uriBuilder.append("&build=1");
    }

    @And("User sees the carbon footprint of a specific build")
    public void userSeesTheCarbonFootprintOfASpecificBuild() {
        int pipelineRuns = AMOUNT_OF_PIPELINES * AMOUNT_OF_BRANCHES;
        validateResponseWithFilter(pipelineRuns, null);
    }

    @And("A specific branch")
    public void aSpecificBranch() {
        uriBuilder.append("&branch=branch1");
    }

    @And("User sees the carbon footprint of a specific branch")
    public void userSeesTheCarbonFootprintOfASpecificBranch() {
        int pipelineRuns = AMOUNT_OF_PIPELINES * AMOUNT_OF_BUILDS;
        validateResponseWithFilter(pipelineRuns, null);
    }

    private Map<String, String> getQueryParamsFromMetaBuilder() {
        Map<String, String> queryParams = new HashMap<>();
        JenkinsMetadata build = metadataBuilder.build();
        String job = build.getJob();
        String branch = build.getBranch();
        Long buildNumber = build.getBuildNumber();
        if (job != null) {
            queryParams.put("job", job);
        }
        if (branch != null) {
            queryParams.put("branch", branch);
        }
        if (buildNumber != null) {
            queryParams.put("build", buildNumber.toString());
        }
        return queryParams;
    }

    @Given("User wants to see the carbon footprint of a non-existant Jenkins pipeline")
    public void userWantsToSeeTheCarbonFootprintOfANonExistantJenkinsPipeline() {
        uriBuilder.append("?job=random");
    }

    @And("User sees the no carbon footprint")
    public void userSeesTheNoCarbonFootprint() {
        JsonPath jsonPath = context.response.getBody().jsonPath();
        assertEquals("0", jsonPath.get("powerUsageKwh"));
        assertEquals("0", jsonPath.get("pipelineRuns"));
        assertEquals("0", jsonPath.get("runtimeSeconds"));
        assertEquals("0", jsonPath.get("co2ProducedTotalGr"));
        assertEquals("0", jsonPath.get("co2ProducingPerRunGr"));
        assertEquals("0", jsonPath.get("co2ProducingPerHourGr"));
        assertEquals("0.00", jsonPath.get("cpuUtilization"));
    }


    private void validateResponseWithFilter(int pipelineRuns, Integer price) {
        int runtimeSeconds = PIPELINE_DURATION_MINUTES * 60 * pipelineRuns;
        float joules = runtimeSeconds * JOULES;
        BigDecimal runtimeInHours = MathUtils.calculateRuntimeInHours(runtimeSeconds);
        BigDecimal watts = MathUtils.calculateWatts(BigDecimal.valueOf(joules), runtimeSeconds);
        BigDecimal kwh = MathUtils.calculateKwh(watts);
        BigDecimal kwhUsed = MathUtils.calculateKwhUsed(kwh, runtimeInHours);
        BigDecimal kwhPerPipeline = MathUtils.calculateKwhPerPipeline(kwhUsed, pipelineRuns);
        BigDecimal carbonIntensity = BigDecimal.valueOf(DEFAULT_CARBON_INTENSITY);
        BigDecimal co2ProducedTotalGr = MathUtils.calculateCo2ProducedTotal(kwhUsed, carbonIntensity);
        BigDecimal co2ProducingPerRunGr = MathUtils.calculateCo2ProducingPerRun(kwhPerPipeline, carbonIntensity);
        BigDecimal co2ProducingPerHourGr = MathUtils.calculateCo2ProducingPerHour(kwh, carbonIntensity);
        JsonPath jsonPath = context.response.getBody().jsonPath();
        assertEquals(prettyPrintBigDecimal(kwhUsed), jsonPath.get("powerUsageKwh"));
        assertEquals(String.valueOf(pipelineRuns), jsonPath.get("pipelineRuns"));
        assertEquals(String.valueOf(runtimeSeconds), jsonPath.get("runtimeSeconds"));
        assertEquals(prettyPrintBigDecimal(co2ProducedTotalGr), jsonPath.get("co2ProducedTotalGr"));
        assertEquals(prettyPrintBigDecimal(co2ProducingPerRunGr), jsonPath.get("co2ProducingPerRunGr"));
        assertEquals(prettyPrintBigDecimal(co2ProducingPerHourGr), jsonPath.get("co2ProducingPerHourGr"));
        assertEquals(prettyPrintUtilization(BigDecimal.valueOf(UTILIZATION)), jsonPath.get("cpuUtilization"));
        if (price != null) {
            BigDecimal priceBigDecimal = BigDecimal.valueOf(price);
            BigDecimal priceTotal = MathUtils.calculatePriceOfKwh(kwhUsed, priceBigDecimal);
            BigDecimal pricePerRun = MathUtils.calculatePriceOfKwh(kwhPerPipeline, priceBigDecimal);
            BigDecimal pricePerHour = MathUtils.calculatePriceOfKwh(kwh, priceBigDecimal);
            assertEquals(prettyPrintBigDecimal(priceTotal), jsonPath.get("priceTotal"));
            assertEquals(prettyPrintBigDecimal(pricePerRun), jsonPath.get("pricePerRun"));
            assertEquals(prettyPrintBigDecimal(pricePerHour), jsonPath.get("pricePerHour"));
        } else {
            assertNull(jsonPath.get("priceTotal"));
            assertNull(jsonPath.get("pricePerHour"));
            assertNull(jsonPath.get("pricePerRun"));
        }
    }

    private void validateResponseAll(int pipelineRuns, Integer price) {
        int runtimeSeconds = PIPELINE_DURATION_MINUTES * 60 * pipelineRuns;
        float joules = runtimeSeconds * JOULES;
        BigDecimal runtimeInHours = MathUtils.calculateRuntimeInHours(runtimeSeconds);
        BigDecimal watts = MathUtils.calculateWatts(BigDecimal.valueOf(joules), runtimeSeconds);
        BigDecimal kwh = MathUtils.calculateKwh(watts);
        BigDecimal kwhUsed = MathUtils.calculateKwhUsed(kwh, runtimeInHours);
        BigDecimal carbonIntensity = BigDecimal.valueOf(DEFAULT_CARBON_INTENSITY);
        BigDecimal co2ProducedTotalGr = MathUtils.calculateCo2ProducedTotal(kwhUsed, carbonIntensity);
        JsonPath jsonPath = context.response.getBody().jsonPath();
        assertEquals(prettyPrintBigDecimal(kwhUsed), jsonPath.get("powerUsageKwh"));
        assertEquals(String.valueOf(pipelineRuns), jsonPath.get("pipelineRuns"));
        assertEquals(String.valueOf(runtimeSeconds), jsonPath.get("runtimeSeconds"));
        assertEquals(prettyPrintBigDecimal(co2ProducedTotalGr), jsonPath.get("co2ProducedTotalGr"));
        assertNull(jsonPath.get("co2ProducingPerRunGr"));
        assertNull(jsonPath.get("co2ProducingPerHourGr"));
        assertEquals(prettyPrintUtilization(BigDecimal.valueOf(UTILIZATION)), jsonPath.get("cpuUtilization"));
        if (price != null) {
            BigDecimal priceTotal = MathUtils.calculatePriceOfKwh(kwhUsed, BigDecimal.valueOf(price));
            assertEquals(prettyPrintBigDecimal(priceTotal), jsonPath.get("priceTotal"));
        } else {
            assertNull(jsonPath.get("priceTotal"));
        }
    }

}
